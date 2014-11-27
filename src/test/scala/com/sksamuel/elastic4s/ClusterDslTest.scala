package com.sksamuel.elastic4s

import java.util.concurrent.TimeUnit

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.common.unit.TimeValue
import org.scalatest.{ FlatSpec, Matchers }

class ClusterDslTest extends FlatSpec with Matchers {

  "a cluster health request" should "accept empty indices" in {
    val req = clusterHealth
    req.build.indices() should equal(Array())
  }

  it should "accept a list of indices" in {
    val req = clusterHealth("index1", "index2")
    req.build.indices() should equal(Array("index1", "index2"))
  }

  it should "accept a timeout" in {
    val req = clusterHealth timeout "1s"
    req.build.timeout().millis() should equal(new TimeValue(1, TimeUnit.SECONDS).millis())
  }
}
