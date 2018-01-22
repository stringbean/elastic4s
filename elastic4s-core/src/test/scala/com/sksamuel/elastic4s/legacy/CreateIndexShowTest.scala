package com.sksamuel.elastic4s.legacy

import com.sksamuel.elastic4s.legacy.mappings.FieldType.{IntegerType, StringType}
import org.scalatest.{WordSpec, Matchers}

class CreateIndexShowTest extends WordSpec with Matchers with ElasticSugar {

  import ElasticDsl._
  import scala.concurrent.duration._

  "CreateIndex" should {
    "have a show typeclass implementation" in {
      val request = {
        create index "gameofthrones" mappings(
          mapping name "characters" fields(
            field name "name" typed StringType,
            field name "location" typed StringType
            ) timestamp true ttl false,
          mapping name "locations" fields(
            field name "name" typed StringType,
            field name "continent" typed StringType,
            field name "iswinter" typed IntegerType
            ) all true source true numericDetection false
          ) refreshInterval 10.seconds shards 4 replicas 2
      }
      request.show shouldBe """{"settings":{"index":{"number_of_shards":4,"number_of_replicas":2,"refresh_interval":"10000ms"}},"mappings":{"characters":{"_timestamp":{"enabled":true},"_ttl":{"enabled":false},"properties":{"name":{"type":"string"},"location":{"type":"string"}}},"locations":{"_all":{"enabled":true},"_source":{"enabled":true},"numeric_detection":false,"properties":{"name":{"type":"string"},"continent":{"type":"string"},"iswinter":{"type":"integer"}}}}}"""
    }
  }
}
