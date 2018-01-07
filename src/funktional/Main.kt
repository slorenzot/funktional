package funktional

/**
 * Created by Redes on 16/12/2017.
 */
fun main(args: Array<String>) {

    if (Option("Hi") is Option.Some) {
        println("Some")
    }

    var str: String? = "Hi"
    str = null

    Option.empty()
    Option("Hi").ifPresent { println(it.value) }
    Option("").ifPresentOrElse({ println(it.value) }, { println("Not Present") })

    if (Option("Hi").notNull()) println("is not null")

    Future({
        val s: String? = "Cumaná"
        s!!.capitalize()
    }, { value -> println(value) }, { error -> println(error) })

}