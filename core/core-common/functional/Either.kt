package com.meadow.core.common.functional


sealed class Either<out L, out R> {
    data class Left<out L>(val value: L) : Either<L, Nothing>()
    data class Right<out R>(val value: R) : Either<Nothing, R>()

    val isLeft: Boolean get() = this is Left<L>
    val isRight: Boolean get() = this is Right<R>

    inline fun <T> fold(left: (L) -> T, right: (R) -> T): T = when (this) {
        is Left -> left(value)
        is Right -> right(value)
    }

    inline fun <T> map(transform: (R) -> T): Either<L, T> = when (this) {
        is Left -> this
        is Right -> Right(transform(value))
    }

    inline fun <LL> mapLeft(transform: (L) -> LL): Either<LL, R> = when (this) {
        is Left -> Left(transform(value))
        is Right -> this
    }

    companion object {
        fun <R> right(value: R): Either<Nothing, R> = Right(value)
        fun <L> left(value: L): Either<L, Nothing> = Left(value)
    }
}
