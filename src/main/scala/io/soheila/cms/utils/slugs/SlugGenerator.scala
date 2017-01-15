package io.soheila.cms.utils.slugs

import java.text.Normalizer
import java.time.LocalDateTime
import io.soheila.cms.utils.slugs.StringImplicits._

object SlugGenerator {
  def generate(): String = {
    generate(LocalDateTime.now().toString)
  }

  /**
   * Returns slug based on input string.
   *
   * @param input input string to be used for generating slug.
   * @return slug based on input string or based on the local date if no String is provided.
   */
  def generate(input: String): String = {
    var slug = generateInitialSlug(input)

    // Go lower case and transform whitespace
    slug = slug.trim.toLowerCase
    slug = RegexPatterns.WHITESPACE.matcher(slug).replaceAll("-")

    // Special chars
    slug = Normalizer.normalize(slug, Normalizer.Form.NFD)
    Mappings.SlUGSPECIALCHARACTER.foreach {
      case (key, value) => slug = slug.replaceAll(key, value)
    }

    // All other chars...
    slug = RegexPatterns.NONLATIN.matcher(slug).replaceAll("")

    // Remove extra dashes
    val isDash: (Char => Boolean) = _ == '-'
    slug.replaceAll("(-){2,}", "-").dropWhile(isDash).dropWhileInverse(isDash)
  }

  def generateUnique(name: String, similarSlugs: (String => List[String])): String = {
    val baseSlug = generate(name)

    var slug = baseSlug
    val existingSlugs = similarSlugs(baseSlug)

    var num = 0
    while (existingSlugs.contains(slug)) {
      num += 1
      slug = baseSlug + "-" + num
    }

    slug
  }

  private def generateInitialSlug(str: String): String = {
    if (str.isEmpty) LocalDateTime.now().toString
    else str
  }

}
