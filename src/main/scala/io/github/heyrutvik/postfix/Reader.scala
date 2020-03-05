package io.github.heyrutvik.postfix

import fastparse._
import NoWhitespace._

import scala.util.Try

object Reader {

  def read(s: String): Either[Exception, TopLevelExpr] = parse(s, topLevelExpr(_)).fold(
    (error, at, _) => Left(new RuntimeException(s"reader error: '$error' at $at")),
    (p, _) => Right(p)
  )

  private def topLevelExpr[_: P]: P[TopLevelExpr] = spaces ~ (program | stack | commandSeq)

  private def program[_: P]: P[Program] = (P("(postfix") ~ spaces ~ stack ~ spaces ~ commandSeq ~ spaces ~ P(")")).map {
    case (stack, seq) => Program.Prog(stack, seq)
  }

  private def stack[_: P]: P[Stack] = (P("[") ~ spaces ~ P(value.rep).map(_.toList) ~ spaces ~ P("]")).map(Stack.St)
  private def value[_: P]: P[Value] = command.map {
    case Cmd.IntLit(n) => Value.IntLit(n)
    case Cmd.Q(CmdSeq.Seq(cs)) => Value.CommandSeq(cs)
    case cmd => throw new RuntimeException(s"value error: '$cmd' is not a value.")
  }

  private def commandSeq[_: P]: P[CmdSeq] = (P("(") ~ spaces ~ P(command.rep) ~ spaces ~ P(")")).map(cs => CmdSeq.Seq(cs.toList))
  private def command[_: P]: P[Cmd] = spaces ~ (singleCommand | execSeq)
  private def singleCommand[_: P]: P[Cmd] = (spaces ~ atom).map {
    case "pop" => Cmd.Pop
    case "swap" => Cmd.Swap
    case "exec" => Cmd.Exec
    case "sel" => Cmd.Sel
    case "nget" => Cmd.Nget
    case "add" => Cmd.A(ArithOp.Add)
    case "sub" => Cmd.A(ArithOp.Sub)
    case "mul" => Cmd.A(ArithOp.Mul)
    case "div" => Cmd.A(ArithOp.Div)
    case "rem" => Cmd.A(ArithOp.Rem)
    case "lt" => Cmd.R(RelOp.Lt)
    case "eq" => Cmd.R(RelOp.Lt)
    case "gt" => Cmd.R(RelOp.Lt)
    case number if Try(number.toInt).isSuccess => Cmd.IntLit(number.toInt)

    // error cases
    case "postfix" => throw new RuntimeException(s"command error: double check your parens. :)")
    case cmd => throw new RuntimeException(s"command error: '$cmd' is not a command.")
  }
  private def execSeq[_: P]: P[Cmd] = commandSeq.map(Cmd.Q)

  private def atom[_: P]: P[String] = for {
    first <- P(letter | digit)
    rest <- P((letter | digit).rep).map(_.toList)
  } yield (first :: rest).mkString

  private def spaces[_: P]: P[Unit] = P(CharIn(" ").rep)
  private def digit[_: P]: P[Char] = P(CharIn("0-9")).!.map(_.charAt(0))
  private def letter[_: P]: P[Char] = P(CharIn("a-zA-Z")).!.map(_.charAt(0))
}
