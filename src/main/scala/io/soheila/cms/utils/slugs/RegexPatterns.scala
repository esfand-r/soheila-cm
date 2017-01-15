package io.soheila.cms.utils.slugs

import java.util.regex.Pattern

object RegexPatterns {
  val WHITESPACE = Pattern.compile("[\\s]")
  val NONLATIN = Pattern.compile("[^\\w-]")
}
