package com.sksamuel.elastic4s.legacy

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.legacy.ElasticDsl._

/** @author Stephen Samuel */
class OptimizeDslTest extends FlatSpec with MockitoSugar with ElasticSugar {

  // not testing the output of these; they are here just to test the DSL in the client
  client.execute {
    create index "test1"
  }.await
  client.execute {
    create index "test2"
  }.await
  client.execute {
    optimize index "test1"
  }.await
  client.execute {
    optimize("test1", "test2")
  }.await
  client.execute {
    optimize index Seq("test1", "test2")
  }.await
  client.execute {
    optimize("test1")
  }.await

  "an optimize request" should "accept var args" in {
    val opt = optimize("index1", "index2").maxSegments(5)
    assert(opt.build.indices() === Array("index1", "index2"))
    assert(opt.build.maxNumSegments() === 5)
  }

  it should "accept single index version" in {
    val opt = optimize index "index1" flush true
    assert(opt.build.indices() === Array("index1"))
    assert(opt.build.flush())
  }

  it should "accept single seq" in {
    val opt = optimize index Seq("index1", "index2") flush true
    assert(opt.build.indices() === Array("index1", "index2"))
    assert(opt.build.flush())
  }

  it should "accept var arg method invocation" in {
    val opt = optimize("index1", "index2") flush true
    assert(opt.build.indices() === Array("index1", "index2"))
    assert(opt.build.flush())
  }

  it should "accept single string method invocation" in {
    val opt = optimize("index1") flush true
    assert(opt.build.indices() === Array("index1"))
    assert(opt.build.flush())
  }
}
