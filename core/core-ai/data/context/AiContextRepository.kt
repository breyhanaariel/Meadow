package com.meadow.core.ai.data.context

import com.meadow.core.ai.domain.model.AiPersona
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiContextRepository @Inject constructor(
    private val dao: AiContextDao
) {

    companion object {
        const val MAX_INJECTED_CONTEXTS = 5
    }

    fun parsePersonaKeysCsv(csv: String?): Set<String>? {
        val raw = csv?.trim().orEmpty()
        if (raw.isBlank()) return null
        return raw.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toSet()
            .ifEmpty { null }
    }

    private fun appliesToPersona(entity: AiContextEntity, persona: AiPersona): Boolean {
        val keys = parsePersonaKeysCsv(entity.personaKeysCsv)
        return keys == null || keys.contains(persona.name)
    }

    suspend fun resolveInjectedContexts(
        persona: AiPersona,
        scopeKey: String?,
        includeScoped: Boolean
    ): List<AiContextEntity> {

        val personaContexts = dao.listEnabledGlobalContexts()
            .filter { appliesToPersona(it, persona) }

        val scopedContexts =
            if (includeScoped && !scopeKey.isNullOrBlank()) {
                dao.listEnabledScopedContexts(scopeKey).filter { appliesToPersona(it, persona) }
            } else emptyList()

        // Persona contexts MUST come before scoped contexts
        val merged = ArrayList<AiContextEntity>(personaContexts.size + scopedContexts.size)
        merged.addAll(personaContexts)
        merged.addAll(scopedContexts)

        return merged
            .distinctBy { it.id }
            .take(MAX_INJECTED_CONTEXTS)
    }

    fun buildContextBlock(contexts: List<AiContextEntity>): String {
        if (contexts.isEmpty()) return ""
        return buildString {
            appendLine("CONTEXTS:")
            contexts.forEachIndexed { idx, c ->
                appendLine("${idx + 1}. ${c.title} (${c.category})")
                appendLine(c.text.trim())
                appendLine()
            }
        }.trim()
    }

    suspend fun upsert(
        id: String? = null,
        title: String,
        text: String,
        category: String,
        scopeKey: String?,
        personaKeysCsv: String?,
        enabled: Boolean,
        pinned: Boolean
    ): String {
        val now = System.currentTimeMillis()
        val finalId = id ?: UUID.randomUUID().toString()
        val existing = dao.getContextById(finalId)

        dao.upsert(
            AiContextEntity(
                id = finalId,
                title = title.trim(),
                text = text,
                category = category.trim(),
                scopeKey = scopeKey,
                personaKeysCsv = personaKeysCsv,
                enabled = enabled,
                pinned = pinned,
                createdAtUtcMs = existing?.createdAtUtcMs ?: now,
                updatedAtUtcMs = now
            )
        )

        return finalId
    }

    suspend fun delete(id: String) {
        dao.deleteContextById(id)
    }

    suspend fun listGlobalAll(): List<AiContextEntity> =
        dao.listGlobalContextsAll()

    suspend fun listScopedContextsAll(scopeKey: String): List<AiContextEntity> =
        dao.listScopedContextsAll(scopeKey)

    suspend fun listForUiFiltered(
        persona: AiPersona?,
        scopeKey: String?
    ): List<AiContextEntity> {

        val base = if (scopeKey == null) {
            dao.listGlobalContextsAll()
        } else {
            dao.listScopedContextsAll(scopeKey)
        }

        return if (persona == null) {
            base
        } else {
            base.filter { appliesToPersona(it, persona) }
        }
    }

}
