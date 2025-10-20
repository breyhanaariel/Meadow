package com.meadow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.domain.model.PlotCard
import com.meadow.app.data.repository.PlotCardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlotCardsViewModel(
    private val repository: PlotCardsRepository
) : ViewModel() {

    private val _plotCards = MutableStateFlow<List<PlotCard>>(emptyList())
    val plotCards = _plotCards.asStateFlow()

    init { loadCards() }

    fun loadCards() {
        viewModelScope.launch { _plotCards.value = repository.getAll() }
    }

    fun addCard(title: String, summary: String, color: Int) {
        viewModelScope.launch {
            repository.insert(PlotCard(title = title, summary = summary, color = color))
            loadCards()
        }
    }

    fun editCard(card: PlotCard) {
        viewModelScope.launch {
            repository.update(card)
            loadCards()
        }
    }

    fun deleteCard(id: String) {
        viewModelScope.launch {
            repository.delete(id)
            loadCards()
        }
    }
}