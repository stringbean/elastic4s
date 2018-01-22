package com.sksamuel.elastic4s.legacy

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.scalatest.Matchers
import org.scalatest.matchers.{ Matcher, MatchResult }

trait JsonSugar extends Matchers {

  protected val mapper = new ObjectMapper with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)

  def matchJsonResource(resourceName: String) = new JsonResourceMatcher(resourceName)

  class JsonResourceMatcher(resourceName: String) extends Matcher[String] {
    override def apply(left: String): MatchResult = {
      val jsonResource = getClass.getResource(resourceName)
      withClue(s"expected JSON resource [$resourceName] ") { jsonResource should not be null }

      val expectedJson = mapper.readTree(jsonResource)
      val actualJson = mapper.readTree(left)

      MatchResult(
        expectedJson == actualJson,
        s"$actualJson did not match resource [$resourceName]: $expectedJson",
        s"$actualJson did match resource [$resourceName]: $expectedJson"
      )
    }
  }
}
