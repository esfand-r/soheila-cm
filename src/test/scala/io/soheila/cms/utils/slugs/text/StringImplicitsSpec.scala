package io.soheila.cms.utils.slugs.text

import io.soheila.cms.utils.slugs.StringImplicits._
import org.specs2._

class StringImplicitsSpec extends Specification {

  def is =
    s2"""
 This is a specification to check the implicits for string in this utility
 The '"Test  Slug ! Generator :: for Scala 2.12"' string should
   generate slug when called on a string             $scenario1
   generate slug when called on a string and passed a sample that is used
   to egenrate a unique slug against                 $scenario2
   """

  def scenario1 = "Test  Slug ! Generator :: for Soheila 2.12".slugify must beEqualTo("test-slug-generator-for-soheila-2-12")

  val similar: (String => List[String]) = baseSlug => List(baseSlug, "%s-1".format(baseSlug))

  def scenario2 = "Test  Slug ! Generator :: for Soheila 2.12".slugify(similar) must beEqualTo("test-slug-generator-for-soheila-2-12-2")
}
