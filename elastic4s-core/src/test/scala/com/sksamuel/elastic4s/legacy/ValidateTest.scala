package com.sksamuel.elastic4s.legacy

import com.sksamuel.elastic4s.legacy.ElasticDsl._
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

/** @author Stephen Samuel */
class ValidateTest extends WordSpec with ElasticSugar with Matchers {

  implicit val duration: Duration = 10.seconds

  client.execute {
    index into "food/pasta" fields(
      "name" -> "maccaroni",
      "color" -> "yellow"
      )
  }.await

  blockUntilCount(1, "food")

  "a validate query" should {
    "return valid when the query is valid for a string query" in {
      val resp = client.execute {
        validate in "food/pasta" query "maccaroni"
      }.await
      resp.isValid shouldBe true
    }
    "return valid when the query is valid for a dsl query" in {
      val resp = client.execute {
        validate in "food/pasta" query {
          bool {
            should {
              termQuery("name", "maccaroni")
            }
          }
        }
      }.await
      resp.isValid shouldBe true
    }
  }
}
