package com.vicker.study.project.reactor

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink
import reactor.util.function.Tuple2
import reactor.util.function.Tuple3
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

object MonoCreationUsage {

    /**
     * @see justOrEmpty
     */
    private fun just() {
        start("just")
        Mono.just("just Hello World no subscribe.").subscribe()
        Mono.just("just Hello World with ref function").subscribe(::println)
        Mono.just("just Hello World with lambda").subscribe { str -> println(str) }
        Mono.just("just Hello World with anon function").subscribe(fun(str: String) { println(str) })
        end()
    }

    /**
     * @see just
     * @see empty
     */
    private fun justOrEmpty() {
        start("just or empty")
        Mono.justOrEmpty("justOrEmpty hello World").subscribe(::println);
        Mono.justOrEmpty(Optional.ofNullable("justOrEmpty hello world")).subscribe(::println);
        end()
    }

    /**
     * 从Supplier接口中创建mono
     * @see MonoCreationUsage.fromCallable
     */
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

    /**
     * 从一个Publisher中创建mono
     * @see MonoCreationUsage.fromDirect
     */
    private fun from() {
        start("from")
        Mono.from(Mono.just("from hello world with mono")).subscribe(::println)
        Mono.from(Flux.just("first from hello world with flux", "second from hello world with flux")).subscribe(::println)
        end()
    }

    /**
     * 从callable接口中创建mono，未发现与Supplier有啥区别，源码实现也一样
     * @see MonoCreationUsage.fromSupplier
     */
    private fun fromCallable() {
        start("from callable")
        Mono.fromCallable { "from callable hello world" }.subscribe(::println)
        end()
    }

    /**
     * 创建空的mono，completes时执行runnable
     */
    private fun fromRunnable() {
        start("from runnable")
        Mono.fromRunnable<String> { println("from runnable hello world") }.subscribe()
        end()
    }

    /**
     * 从CompletableFuture中获取，异步的
     * @see MonoCreationUsage.fromCompletableStage
     * @see defer
     */
    private fun fromFuture() {
        start("from future")
        Mono.fromFuture { CompletableFuture.supplyAsync { "from future hello world with supply async and lambda" } }.subscribe(::println)
        Mono.fromFuture(CompletableFuture.supplyAsync { "from future hello world with supply async" }).subscribe(::println)
        end()
    }

    /**
     * @see MonoCreationUsage.from
     */
    private fun fromDirect() {
        start("from direct")
        Mono.fromDirect(Mono.just("from direct hello world with mono")).subscribe(::println)
        Mono.fromDirect(Flux.just("first from direct hello world with flux", "second from direct hello world with flux")).subscribe(::println)
        end()
    }

    /**
     * 从CompletableStage中获取，与fromFuture相似
     * @see MonoCreationUsage.fromFuture
     * @see defer
     */
    private fun fromCompletableStage() {
        start("from completable stage")
        Mono.fromCompletionStage(CompletableFuture.completedStage("from completable stage hello world")).subscribe(::println)
        Mono.fromCompletionStage { CompletableFuture.completedFuture("from completable stage hello world with supplier") }.subscribe(::println)
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
        fromDirect()
        fromCompletableStage()
        defer()
        deferWithContext()
        delay()
        empty()
        error()
        never()
        zip()
    }

    /**
     * 将多个mono压缩成一个mono使用，所有子mono完成并合并结果，才会触发zip的mono，供zip消费。最多只支持8个，不够用时，可考虑嵌套zip
     * 可用于需要获取所有mono的结果才能继续下一步。（与CountDownLatch, CompletableFuture.allOf所达到的目标相同）
     */
    private fun zip() {
        start("zip")
        Mono.zip(Mono.just("1"), Mono.just("2")).subscribe(::println)
        Mono.zip(Mono.just("1"), Mono.just("2"), Mono.just("3")).subscribe(::println)
        Mono.zip(Mono.just(1), Mono.just("2"), Mono.just(true))
                .doOnSuccess { tuple: Tuple3<Int, String, Boolean> -> println(tuple) }.subscribe(::println)
        Mono.zip(Mono.fromFuture { CompletableFuture.supplyAsync { 1 } }, Mono.just("aaa")).subscribe(::println)
        Mono.zip(Function<Array<Any>, Data> { a: Array<Any> ->
            Data(a[0] as String, a[1] as Int, a[2] as Boolean)
        }, Mono.just("zhang san"), Mono.just(18), Mono.just(true)).subscribe(::println)
        Mono.zip(Mono.empty<Long>(), Mono.just("aaa")).doOnSuccess(object : Consumer<Tuple2<Long?, String?>?> {
            override fun accept(t: Tuple2<Long?, String?>?) {
                println("do On Success $t")
            }
        }).map { t ->
            println("flat map $t")
            t
        }.subscribe(::println)

        end()
    }

    data class Data(val name: String, val age: Int, val gender: Boolean, val amount: Long? = 1)

    private fun never() {
        start("never")
        Mono.never<String>().doOnSuccess { str -> println(str) }.subscribe(::println)
        end()
    }

    private fun deferWithContext() {
        start("defer with context")
        Mono.deferWithContext { context -> Mono.just("defer with context hello world ${context.size()}") }.subscribe(::println)
        end()
    }

    /**
     * 空的Mono
     * @see justOrEmpty
     */
    private fun empty() {
        start("empty")
        Mono.empty<String>().doOnSuccess { str -> println("empty consume in do on success $str") }.subscribe()
        end()
    }

    private fun error() {
        start("error")
        Mono.error<Throwable> { NullPointerException() }.doOnError(::println).subscribe()
        Mono.error<Throwable>(NullPointerException()).doOnError { e -> e.printStackTrace(System.out) }.subscribe()
        end()
    }

    private fun delay() {
        start("delay")
        Mono.delay(Duration.ofNanos(0)).subscribe(::println)
        Thread.sleep(1)
        end()
    }

    /**
     * 延迟发生订阅，真正发生订阅的是其委托对象，返回真实的mono对象，常用于间接产生的Mono
     * @see fromFuture
     * @see fromCompletableStage
     */
    private fun defer() {
        start("defer")
        Mono.defer { Mono.just("defer hello world") }.subscribe(::println)
        end()
    }
}
