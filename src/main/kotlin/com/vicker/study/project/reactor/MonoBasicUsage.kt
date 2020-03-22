package com.vicker.study.project.reactor

import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

object MonoBasicUsage {

    private fun just() {
        start("just")
        Mono.just("just Hello World no subscribe.").subscribe()
        Mono.just("just Hello World with ref function").subscribe(::println)
        Mono.just("just Hello World with lambda").subscribe { str -> println(str) }
        Mono.just("just Hello World with anon function").subscribe(fun(str: String) { println(str) })
        end()
    }

    private fun justOrEmpty() {
        start("just or empty")
        Mono.justOrEmpty("justOrEmpty hello World").subscribe(::println);
        Mono.justOrEmpty(Optional.ofNullable("justOrEmpty hello world")).subscribe(::println);
        end()
    }

    private fun fromSupplier() {
        start("from supplier")
        Mono.fromSupplier(fun(): String {
            return "from supplier hello world"
        }).subscribe(::println)
        Mono.fromSupplier { "from supplier hello world with lambda." }.subscribe { str -> println(str) }
        Mono.fromSupplier(object : Supplier<String> {
            override fun get(): String {
                return "from supplier hello world with anon class"
            }

            override fun hashCode(): Int {
                return super.hashCode()
            }
        }).subscribe(::println)
        end()
    }

    private fun from() {
        start("from")
        Mono.from(Mono.just("from")).subscribe(::println)
        end()
    }

    private fun fromCallable() {
        start("from callable")
        Mono.fromCallable { "from callable hello world" }.subscribe(::println)
        end()
    }

    private fun fromRunnable() {
        start("from runnable")
        Mono.fromRunnable<String> { println("from runnable hello world") }.subscribe()
        end()
    }

    private fun fromFuture() {
        start("from future")
        Mono.fromFuture { CompletableFuture.supplyAsync { "from future hello world with supply async and lambda" } }.subscribe(::println)
        Mono.fromFuture(CompletableFuture.supplyAsync { "from future hello world with supply async" }).subscribe(::println)
        end()
    }

    private fun create() {
        start("create")
        Mono.create(fun(sink: MonoSink<String>) {
            sink.success("create Hello World with anon function")
        }).subscribe(::println)
        Mono.create<String> { sink ->
            sink.success("create Hello World with lambda function")
        }.subscribe(::println)
        end()
    }

    fun execute() {
        justOrEmpty()
        just()
        create()
        fromSupplier()
        from()
        fromCallable()
        fromRunnable()
        fromFuture()
    }
}
