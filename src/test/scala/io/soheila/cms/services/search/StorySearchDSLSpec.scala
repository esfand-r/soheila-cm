package io.soheila.cms.services.search

import org.specs2.Specification
import StorySearchDSL._

class StorySearchDSLSpec extends Specification {
  def is =
    s2"""
 This is a specification to check that parser should
   build Criteria using storyType only                                  $noPredicate
   build Criteria using storyType and title                             $simplePredicateTitle
   build crtieria using type and tags                                   $simplePredicateTags
   build criteria using type, title and tags                            $combinedPredicate
   build criteria using type, title and tags and coordinate             $combinedPredicateWithCoordinate
   """

  def noPredicate = {
    val str0 = """
                 search "Article"
              """
    val response: String = search(new lexical.Scanner(str0)) match {
      case Success(criteria, _) => criteria.toJson.toString()
      case Failure(msg, _) => msg
      case Error(msg, _) => msg
    }

    response must beEqualTo("{\"storyType\":\"Article\"}")
  }

  def simplePredicateTitle = {
    val str1 = """
                search "Article" by title "test"
              """
    val response: String = search(new lexical.Scanner(str1)) match {
      case Success(criteria, _) => criteria.toJson.toString()
      case Failure(msg, _) => msg
      case Error(msg, _) => msg
    }

    response must beEqualTo("{\"$and\":[{\"storyType\":\"Article\"},{\"title\":\"test\"}]}")
  }

  def simplePredicateTags = {
    val str = """
                search "Article" by tags "a","b"
              """

    val response: String = search(new lexical.Scanner(str)) match {
      case Success(criteria, _) => criteria.toJson.toString()
      case Failure(msg, _) => msg
      case Error(msg, _) => msg
    }

    response must beEqualTo("{\"$and\":[{\"storyType\":\"Article\"},{\"tags\":{\"$in\":[\"a\",\"b\"]}}]}")
  }

  def combinedPredicate = {
    val str = """
                search "Article" by title "test" and tags "a","b"
              """

    val response: String = search(new lexical.Scanner(str)) match {
      case Success(criteria, _) => criteria.toJson.toString()
      case Failure(msg, _) => msg
      case Error(msg, _) => msg
    }

    response must beEqualTo("{\"$and\":[{\"storyType\":\"Article\"},{\"$and\":[{\"title\":\"test\"},{\"tags\":{\"$in\":[\"a\",\"b\"]}}]}]}")
  }

  def combinedPredicateWithCoordinate = {
    val str = """
                search "Article" by title "test" and tags "a","b" and location "-0.0989392","51.5081062"
              """

    val response: String = search(new lexical.Scanner(str)) match {
      case Success(criteria, _) => criteria.toJson.toString()
      case Failure(msg, _) => msg
      case Error(msg, _) => msg
    }

    response must beEqualTo("{\"$and\":[{\"storyType\":\"Article\"},{\"$and\":[{\"$and\":[{\"title\":\"test\"},{\"tags\":{\"$in\":[\"a\",\"b\"]}}]},{\"coordinate\":{\"$near\":{\"$geometry\":{\"type\":\"Point\",\"coordinates\":[-0.0989392,51.5081062]},\"$maxDistance\":10000,\"$minDistance\":1}}}]}]}")
  }
}
