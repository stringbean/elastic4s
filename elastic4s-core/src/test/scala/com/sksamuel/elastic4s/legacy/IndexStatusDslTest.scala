package com.sksamuel.elastic4s.legacy

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.legacy.ElasticDsl._

class IndexStatusDslTest extends FlatSpec with MockitoSugar with ElasticSugar {
  "an index status request" should "accept a single index" in {
    val req = status("status-index")
    assert(req.build.indices() === Array("status-index"))
  }

  it should "accept multiple indices" in {
    val req = status("index1", "index2")
    assert(req.build.indices() === Array("index1", "index2"))
  }
}
