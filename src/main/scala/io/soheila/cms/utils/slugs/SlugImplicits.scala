package io.soheila.cms.utils.slugs

import scala.language.implicitConversions

class SlugifiedString(s: String) {
  /**
   * Enriches a string with slugify.
   */
  def slugify: String = SlugGenerator.generate(s)

  /**
   * Creates unique slug based on existing slug.
   */
  def slugify(p: (String => List[String])): String = SlugGenerator.generateUnique(s, p)

  def dropWhileInverse(p: Char => Boolean): String = s.dropRight(suffixLength(p, expectTrue = true))

  private def suffixLength(p: Char => Boolean, expectTrue: Boolean): Int = {
    var i = 0
    while (i < s.length && p(s.apply(s.length - i - 1)) == expectTrue) i += 1
    i
  }

}

object StringImplicits {
  implicit def toRichString(s: String): SlugifiedString = new SlugifiedString(s)
}
