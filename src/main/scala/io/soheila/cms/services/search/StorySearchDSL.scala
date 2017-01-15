package io.soheila.cms.services.search

import io.soheila.cms.services.search.Criteria.{ And, Eq, In, Near, Or }
import io.soheila.cms.types.StoryType

import scala.language.postfixOps
import scala.util.parsing.combinator._
import scala.util.parsing.combinator.syntactical._

object StorySearchDSL extends StandardTokenParsers {
  lexical.reserved +=
    ("to", "and", "or", "not", "max", "for", "at", "in", "by", "search", "title", "tags", "location")

  lexical.delimiters += ("(", ")", ",", " ")

  private def combinedPredicate: Parser[Seq[Criteria]] = predicate ~ rep(("and" | "or") ~ predicate) ^^ {
    case left ~ right =>
      var pointer = left
      right.foreach {
        case "and" ~ rightOp =>
          val list = pointer ++ rightOp
          pointer = Seq(And(list: _*))
        case "or" ~ rightOp =>
          val list = pointer ++ rightOp
          pointer = Seq(Or(list: _*))
      }
      pointer
  }

  lazy val predicate: Parser[Seq[Criteria]] = "(" ~> combinedPredicate <~ ")" | simplePredicate

  lazy val simplePredicate: Parser[Seq[Criteria]] = (title | tags | coordinates) ^^ (m => Seq(m))

  lazy val title: Parser[Criteria] =
    "title" ~> stringLit ^^ (s => Eq("title", s))

  lazy val tags: Parser[Criteria] =
    "tags" ~> rep1sep(stringLit, ",") ^^ (s => { In("tags", s) })

  lazy val coordinates: Parser[Criteria] =
    "location" ~> rep1sep(stringLit, ",") ^^ (s => {
      Near("coordinate", s(0).toDouble, s(1).toDouble)
    })

  lazy val story_type: Parser[Criteria] =
    "search" ~> stringLit ^^ (
      s => StoryType.values.find(_.toString == s).getOrElse(StoryType.UnAssigned)
    // Avoid Scala enumeration  withName to avoid exception
    ) ^? ({ case sType if sType != StoryType.UnAssigned => Eq("storyType", sType.toString) },
        _ => "Type of the story must be specified. Supported types = {Article}")

  lazy val searchWithPredicate: Parser[Seq[Criteria]] = "by" ~ combinedPredicate ^^ {
    case "by" ~ a => a
  }

  // Matches on the string with longest characters using |||
  lazy val search: Parser[Criteria] = ((story_type+) ~ (searchWithPredicate ?)) ^^ {
    case i ~ a =>
      //      val bla = a.foreach(
      //        a => a match {
      //          case and: And => and.cs.seq
      //          case or: Or => or.cs.seq
      //          case in: In => Seq(in)
      //          case _ =>
      //        }
      //      )
      val seq1: Seq[Criteria] = i
      val seq2: Seq[Criteria] = a.getOrElse(Seq())

      if (a.isEmpty) seq1(0) else {
        val list = seq1 ++ seq2
        And(list: _*)
      }
  }
}
