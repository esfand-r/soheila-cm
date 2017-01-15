package io.soheila.cms.services.search

/**
 * Created by esfandiaramirrahimi on 2017-01-05.
 */
object main {
  def main(args: Array[String]) {

    val str0 = """
                search "Article"
              """

    val str1 = """
                search "Article" by tags "a","b"
              """

    val str2 = """
                search "Article" by title "test"
              """

    val str3 = """
                search "Article" by title "test" and tags "a","b"
              """

    val str4 = """
                search "Article" by title "test" and tags "a","b" and location "-0.0989392","51.5081062"
              """

    import StorySearchDSL._

    search(new lexical.Scanner(str0)) match {
      case Success(order, _) => println(order.toJson)
      case Failure(msg, _) => println("Failure: " + msg)
      case Error(msg, _) => println("Error: " + msg)
    }

    search(new lexical.Scanner(str1)) match {
      case Success(order, _) => println(order.toJson)
      case Failure(msg, _) => println("Failure: " + msg)
      case Error(msg, _) => println("Error: " + msg)
    }

    search(new lexical.Scanner(str2)) match {
      case Success(order, _) => println(order.toJson)
      case Failure(msg, _) => println("Failure: " + msg)
      case Error(msg, _) => println("Error: " + msg)
    }

    search(new lexical.Scanner(str3)) match {
      case Success(order, _) => println(order.toJson)
      case Failure(msg, _) => println("Failure: " + msg)
      case Error(msg, _) => println("Error: " + msg)
    }

    search(new lexical.Scanner(str4)) match {
      case Success(order, _) => println(order.toJson)
      case Failure(msg, _) => println("Failure: " + msg)
      case Error(msg, _) => println("Error: " + msg)
    }
  }
}
