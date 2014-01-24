package com.sksamuel.elastic4s

import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import com.fasterxml.jackson.databind.ObjectMapper
import scala.collection.JavaConverters._

/** @author Stephen Samuel */
class IndexDslTest extends FlatSpec with MockitoSugar with OneInstancePerTest {

  val mapper = new ObjectMapper()

  "an index dsl" should "accept index and type as a / delimited string" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/index_test1.json"))
    val req = index.into("twitter/tweets").id("thisid").fields {
      "name" -> "sksamuel"
    }
    assert(json === mapper.readTree(req._fieldsAsXContent.string))
  }

  it should "accept index and type as a tuple" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/index_test2.json"))
    val req = index into "twitter" -> "tweets" fields {
      "name" -> "sksamuel"
    }
    assert(json === mapper.readTree(req._fieldsAsXContent.string))
  }

  it should "generate json for all fields" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/index_test3.json"))
    val req = index into "twitter/tweet" id 1234 fields(
      "user" -> "sammy",
      "post_date" -> "2009-11-15T14:12:12",
      "message" -> "trying out Elastic Search Scala DSL"
    )
    assert(json === mapper.readTree(req._fieldsAsXContent.string))
  }

  it should "generate json for fields when using a map" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/index_test4.json"))
    val req = index into "twitter/tweet" id 1234 fields Map("user" -> "sammy",
      "post_date" -> "2009-11-15T14:12:12",
      "message" -> "trying out Elastic Search Scala DSL")
    assert(json === mapper.readTree(req._fieldsAsXContent.string))
  }

  it should "not include id when id is not specified" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/index_test5.json"))
    val req = index into "twitter/tweet" fields(
      "user" -> "sammy",
      "post_date" -> "2009-11-15T14:12:12",
      "message" -> "trying out Elastic Search Scala DSL"
    )
    assert(json === mapper.readTree(req._fieldsAsXContent.string))
  }

  it should "include id when id is specified" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/index_test6.json"))
    val req = index into "twitter/tweet" id 9999 fields(
      "user" -> "sammy",
      "post_date" -> "2011-11-15T14:12:12",
      "message" -> "I have an ID"
    ) routing "users" ttl 100000
    assert(json === mapper.readTree(req._fieldsAsXContent.string))
  }

  it should "generate json for array fields" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/index_test7.json"))
    val req = index into "twitter/tweet" id 1234 fields(
      "user" -> "sammy",
      "post_date" -> "2011-11-15T14:12:12",
      "message" -> Array(
          "first message",
          "second message"
      )
    )
    assert(json === mapper.readTree(req._fieldsAsXContent.string))
  }

  it should "generate json for array of nested fields" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/index_test8.json"))
    val req = index into "twitter/tweet" id 1234 fields(
      "user" -> "sammy",
      "post_date" -> "2011-11-15T14:12:12",
      "message" -> Array(
        Map("id" -> 1, "body" -> "first message"),
        Map("id" -> 2, "body" -> "second message")
      )
    )
    assert(json === mapper.readTree(req._fieldsAsXContent.string))
  }

  it should "generate json for array of nested fields v2" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/index_test8.json"))
    val req = index into "twitter/tweet" id 1234 fields(
      "user" -> "sammy",
      "post_date" -> "2011-11-15T14:12:12",
      "message" -> Array(
        Seq("id" -> 1, "body" -> "first message"),
        Seq("id" -> 2, "body" -> "second message")
      )
      )
    assert(json === mapper.readTree(req._fieldsAsXContent.string))
  }
}
