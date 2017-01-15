package io.soheila.cms.utils.slugs.text

import io.soheila.cms.utils.slugs.SlugGenerator
import org.specs2.Specification

class SlugGeneratorSpec extends Specification {

  def is =
    s2"""
 This is a specification to check the slugification that should
   remove extra whitespaces             $whiteSpaceCheck
   change case to lower                 $lowercaseCheck
   change special character to normal
   according to the map                 $specialCharacterCheck
   remove extra dash                    $dashCheck
   convert dot to dash                  $dotCheck
   convert underline to dash            $underlineCheck
   generate a unique slug
   when given the existing one          $uniqueWithSampleCheck
   """

  def whiteSpaceCheck = {
    SlugGenerator.generate(" ") must beEqualTo("")
    SlugGenerator.generate("   ") must beEqualTo("")
    SlugGenerator.generate(" esfand ") must beEqualTo("esfand")
  }

  def lowercaseCheck = {
    SlugGenerator.generate("EsFand") must beEqualTo("esfand")
  }

  def specialCharacterCheck = {
    SlugGenerator.generate("ÀÁÂÃÄÅÇÈÉÊËÌÍÎÏÑÒÓÔÕÖÙÚÛÜÝß") must beEqualTo("aaaaaaceeeeiiiinooooouuuuyb")
    SlugGenerator.generate("àáâãäåçèéêëìíîïñòóôõöùúûüýÿ") must beEqualTo("aaaaaaceeeeiiiinooooouuuuyy")
    SlugGenerator.generate("ĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥ") must beEqualTo("aaaaaaccccccccddddeeeeeeeeeegggggggghh")
    SlugGenerator.generate("ĦħĨĩĪīĬĭĮįİıĲĳĴĵĶķĸĹĺĻļĽľĿŀŁłŃńŅņŇňŉŊŋ") must beEqualTo("hhiiiiiiiiiiijijjjkkkllllllllllnnnnnnnnn")
    SlugGenerator.generate("ŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŸŹźŻżŽžſ") must beEqualTo("oooooooeoerrrrrrssssssssttttttuuuuuuuuuuuuwwyyyzzzzzzs")
    SlugGenerator.generate("ÄäÜüÖöß") must beEqualTo("aauuoob")
    SlugGenerator.generate("ÅÆØæøå") must beEqualTo("aaeoaeoa")
    SlugGenerator.generate("+") must beEqualTo("plus")
    SlugGenerator.generate("€") must beEqualTo("e")
    SlugGenerator.generate("£") must beEqualTo("l")
    SlugGenerator.generate("e\ns\tf") must beEqualTo("e-s-f")
    SlugGenerator.generate("\ne\n\ns\tf\t\ta\nn\td") must beEqualTo("e-s-f-a-n-d")
    SlugGenerator.generate("Esf??and!!!") must beEqualTo("esfand")
    SlugGenerator.generate("esf(a)nd") must beEqualTo("esfand")
    SlugGenerator.generate("esf{and}") must beEqualTo("esfand")
  }

  def dashCheck = {
    SlugGenerator.generate("Esfand !") must beEqualTo("esfand")
    SlugGenerator.generate("Esfand !!!") must beEqualTo("esfand")
    SlugGenerator.generate("Esfand : 2.12") must beEqualTo("esfand-2-12")
    SlugGenerator.generate("Esfand   :   2.12") must beEqualTo("esfand-2-12")
    SlugGenerator.generate("Esfand -3") must beEqualTo("esfand-3")
    SlugGenerator.generate("Esfand__2_12") must beEqualTo("esfand-2-12")
  }

  def dotCheck = {
    SlugGenerator.generate("Esfand 2.12") must beEqualTo("esfand-2-12")
  }

  def underlineCheck = {
    SlugGenerator.generate("Esfand 2.12") must beEqualTo("esfand-2-12")
  }

  def uniqueWithSampleCheck = {
    SlugGenerator.generateUnique("Slug Generator", slug => List()) must beEqualTo("slug-generator")
    SlugGenerator.generateUnique("Slug Generator", slug => List("slug-generator")) must beEqualTo("slug-generator-1")
    SlugGenerator.generateUnique("Slug Generator", slug => List("slug-generator", "slug-generator-1")) must beEqualTo("slug-generator-2")
  }
}

