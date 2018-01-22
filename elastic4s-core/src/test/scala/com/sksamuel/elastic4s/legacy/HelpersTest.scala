package com.sksamuel.elastic4s.legacy

import com.sksamuel.elastic4s.legacy.ElasticDsl._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{ FlatSpec, Matchers }

/** @author Stephen Samuel */
class HelpersTest extends FlatSpec with MockitoSugar with ElasticSugar with Matchers {

  client.execute {
    bulk(
      index into "starcraft/races" fields ("name" -> "zerg", "base" -> "hatchery"),
      index into "starcraft/units" fields ("name" -> "hydra", "race" -> "zerg"),
      index into "starcraft/bands" fields ("name" -> "protoss", "base" -> "nexus") id 45
    )
  }.await

  blockUntilCount(3, "starcraft")

  "reindex" should "reindex all documents from source to target" in {
    import scala.concurrent.ExecutionContext.Implicits.global

    client.reindex("starcraft", "games").await

    blockUntilCount(3, "games")

    val resp = client.execute {
      search in "games" query "protoss"
    }.await

    resp.getHits.totalHits() shouldBe 1
  }

}
