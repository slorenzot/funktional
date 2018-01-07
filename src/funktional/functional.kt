package funktional

/**
 * Created by slorenzot on 12/1/2017.
 */

/**
 * Permite hacer comparación de patrones para dos casos al evaluar un valor o expresion recibida como parametro.
 * Retornando dos posibles resultados: Some y None. Some contiene el valor de la expresion evaluada y None es un objeto
 * que indica que no hubo resultado en la evaluación normalmente porque la expresion retorno null.
 *
 * @sample
 * val option = Option("Hello World!")
 * when (option) {
 *     is Option.Some -> println(option.map { it.toUpperCase() })
 *     is Option.None -> println(option)
 * }
 */
interface Monad<T> {
    fun <Q> map(f: (v: T) -> Q): Q
    fun <Q> flatMap(f: (v: T) -> Q): Monad<Q>
}

sealed class Option<T> {

    companion object {
        operator fun <T> invoke(f: () -> T) = if (f() != null) Some(f()) else None<T>()
        operator fun <T> invoke(o: T?) = if (o == null) empty() else Option({ o })

        fun empty() = Option({ null })
    }

    fun notNull() = Option(this) is Some
    fun isNull() = Option(this) is None
    fun ifPresentOrElse(f: (it: Some<T>) -> Unit, g: () -> Unit) {
        val opt = Option(this)
        when (opt) {
            is Some -> f(opt.value as Some)
            is None -> g()
        }
    }

    fun ifPresent(f: (it: Some<T>) -> Unit) {
        ifPresentOrElse(f, {})
    }

    class Some<S>(val value: S) : Option<S>(), Monad<S> {
        override fun <Q> map(f: (v: S) -> Q) = f(value)
        override fun <Q> flatMap(f: (v: S) -> Q) = Some(f(value))
    }

    class None<S> : Option<S>()
//    infix fun match(f: (it: Option<T>) -> Unit) {}

}

sealed class Either {

    companion object {
        operator fun <L, R> invoke(left: () -> L, right: () -> R) =
                when (Try(right)) {
                    is Try.Success -> right()
                    is Try.Failure -> left()
                }
    }

}

sealed class Try<T> {

    companion object {
        operator fun <R> invoke(f: () -> R): Try<R> =
                try {
                    Success(f())
                } catch (e: Exception) {
                    Failure(e)
                }
    }

    class Success<R>(val value: R) : Try<R>()
    class Failure<E>(val e: Exception) : Try<E>()
}

sealed class Future {
    companion object {
        operator fun <T> invoke(f: () -> T,
                                success: (r: T) -> Unit,
                                failure: (e: Exception) -> Unit = {}): Unit {
            val t = Try(f)
            when (t) {
                is Try.Success -> success(t.value)
                is Try.Failure -> failure(t.e )
            }

        }
    }
}
