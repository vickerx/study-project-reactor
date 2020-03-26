package com.vicker.study.project.reactor

fun start(str: String) {
    println("-----------${str}-----------")
}

fun end() {
    println("----------------------")
}

fun end(str: String): Unit {
    println("-----------${str}-----------")
}