package io.github.heyrutvik.postfix

import scala.annotation.tailrec
import scala.io.StdIn._
import Reader._
import Evaluator._
import Printer._
import PrinterInstances._
import scala.io.AnsiColor._

import scala.util.{Failure, Success, Try}

object Repl extends App {

  val prompt = s"${BLUE}${BOLD}postfix> ${RESET}"
  var debug = false

  @tailrec
  def repl: Unit = {
    readLine(prompt) match {
      case ":q" =>
        println(s"See you soon!")
        System.exit(0)
      case ":d+" =>
        debug = true
        println(s"debug message turned on!")
        repl
      case ":d-" =>
        debug = false
        println(s"debug message turned off!")
        repl
      case topLevelExpr =>
        val readTry = Try(
          read(topLevelExpr).map {
            case s @ Stack.St(_) => "stack = " + print[Stack](s)
            case seq @ CmdSeq.Seq(_) => "commands = " + print[CmdSeq](seq)
            case prog @ Program.Prog(_, _) =>
              val config = input(prog)
              "value = " + eval(config, debug)
          }
        )
        readTry match {
          case Success(Right(answer)) => println(answer)
          case Success(Left(message)) => println(message)
          case Failure(ex) => println(ex.getMessage)
        }
        repl
    }
  }

  repl
}
