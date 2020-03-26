package com.vicker.study.project.reactor

import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.publisher.Mono
import java.util.function.Supplier
import java.util.stream.Stream

object FluxCreationUsage {

    fun execute() {
        just()
        create()
        from()
        fromArray()
        fromIterable()
        fromStream()
        concat()
        defer()
        empty()
        never()
        error()
        range()
        interval()
    }

    private fun interval() {
        start("interval")

    }

    private fun range() {
        start("range")
        Flux.range(1, 10).subscribe(::println)
        end()
    }

    private fun error() {
        start("error")
        Flux.error<Throwable>(NullPointerException()).doOnError(::println).subscribe()
        Flux.error<Throwable>(fun(): Throwable { return NullPointerException() }).doOnError(::println).subscribe()
        Flux.error<Throwable> { NullPointerException() }.doOnError(::println).subscribe()
        end()
    }

    private fun never() {
        start("never")
        Flux.never<String>().subscribe { println("never") }
        end()
    }

    private fun empty() {
        start("empty")
        Flux.empty<String>().doOnComplete { println("empty") }.subscribe(::println)
        end()
    }

    private fun defer() {
        start("defer")
        Flux.defer(Supplier { Flux.just("defer hello world") }).subscribe(::println)
        Flux.defer { Flux.just("defer hello world") }.subscribe(::println)
        Flux.defer(fun(): Flux<String> = Flux.just("defer hello world")).subscribe(::println)
        Flux.defer(fun(): Flux<String> {
            return Flux.just("defer hello world")
        }).subscribe(::println)
        end()
    }

    private fun concat() {
        start("concat")
        Flux.concat(Flux.fromArray(arrayOf("flux 1th", "flux 2th")), Mono.fromSupplier<String> { -> "mono" }).subscribe(::println)
        Flux.concat(listOf(Flux.just("just"), Mono.just("mono"))).subscribe(::println)
        Flux.concat(Flux.from(Flux.just("flux from flux")), Mono.from(Mono.just("mono from mono")),
                Mono.fromDirect(Flux.from(Mono.just("mono from flux from mono"))),
                Flux.concat(Flux.just("flux from concat"), Mono.just("flux from mono"))
        ).subscribe(::println)
        end()
    }

    private fun fromStream() {
        start("from stream")

        Flux.fromStream(listOf("first", "second", "thirst").stream()).subscribe(::println)
        //匿名函数
        Flux.fromStream(fun(): Stream<String> { return listOf<String>("first", "second", "thirst").stream() }).subscribe(::println)
        //lambda + 自动类型推断
        Flux.fromStream { -> listOf<String>("first", "second", "thirst").stream() }.subscribe(::println)
        //lambda + 手动指定类型
        Flux.fromStream(Supplier<Stream<out String>> { listOf("first", "second", "thirst").stream() }).subscribe(::println)

        end()
    }

    private fun fromIterable() {
        start("from iterable")
        Flux.fromIterable(listOf("first", "second", "thirst")).subscribe(::println);
        end()
    }

    private fun fromArray() {
        start("from array")
        Flux.fromArray(arrayOf("first", "second")).subscribe(::println)
        end()
    }

    private fun from() {
        start("from")
        Flux.from(Mono.just("from mono")).subscribe(::println)
        end()
    }

    private fun create() {
        start("create")
        Flux.create<String> { fluxSink: FluxSink<String>? -> fluxSink?.next("flux create hello world") }.subscribe(::println)
        end()
    }

    private fun just() {
        start("just")
        Flux.just("flux just hello world").subscribe(::println)
        Flux.just("1th flux just hello world", "2th flux just hello world").subscribe(::println)
        end()
    }
}