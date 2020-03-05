package io.github.heyrutvik.postfix

import io.github.heyrutvik.postfix.Printer.print

trait Printer[T] {
  def print(t: T): String
}

object Printer {
  def print[A](a: A)(implicit pr: Printer[A]): String = pr.print(a)
}

object PrinterInstances {
  implicit val programPrinter: Printer[Program] = {
    case Program.Prog(stack, body) => "(postfix " + print(stack) + " " + print(body) + ")"
  }

  implicit val commandSeqPrinter: Printer[CmdSeq] = {
    case CmdSeq.Seq(cs) => cs.map(print(_)).mkString("(", " ", ")")
  }

  implicit val commandPrinter: Printer[Cmd] = {
    case Cmd.IntLit(n) => n.toString
    case Cmd.Pop => "pop"
    case Cmd.Swap => "swap"
    case Cmd.Nget => "nget"
    case Cmd.Sel => "sel"
    case Cmd.Exec => "exec"
    case Cmd.A(op) => print(op)
    case Cmd.R(op) => print(op)
    case Cmd.Q(cs) => print(cs)
  }

  implicit val arithmeticOperatorPrinter: Printer[ArithOp] = {
    case ArithOp.Add => "add"
    case ArithOp.Sub => "sub"
    case ArithOp.Mul => "mul"
    case ArithOp.Div => "div"
    case ArithOp.Rem => "rem"
  }

  implicit val relationalOperatorPrinter: Printer[RelOp] = {
    case RelOp.Lt => "lt"
    case RelOp.Eq => "eq"
    case RelOp.Gt => "Gt"
  }

  implicit val valuePrinter: Printer[Value] = {
    case Value.IntLit(n) => n.toString
    case Value.CommandSeq(cs) => print[CmdSeq](CmdSeq.Seq(cs))
  }

  implicit val stackPrinter: Printer[Stack] = {
    case Stack.St(vs) => vs.map(print[Value]).mkString("[", " ", "]")
  }
}