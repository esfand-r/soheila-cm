package io.soheila.cms.utils.slugs

object Mappings {
  val SlUGSPECIALCHARACTER = Map(
    "\\+" -> "plus",
    // Latin-1 Supplement
    "ß" -> "b",
    // Latin Extended-A
    "đ" -> "d",
    "ħ" -> "h",
    "ı" -> "i",
    "ĳ" -> "ij",
    "ĸ" -> "k",
    "ŀ" -> "l",
    "ł" -> "l",
    "ŉ" -> "n",
    "ŋ" -> "n",
    "œ" -> "oe",
    "ŧ" -> "t",
    "ſ" -> "s",
    "€" -> "e",
    "£" -> "l",
    "æ" -> "ae",
    "ø" -> "o",

    // Extras
    "\\." -> "-",
    "_" -> "-"
  )
}

