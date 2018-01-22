package com.sksamuel.elastic4s.legacy.admin

import com.sksamuel.elastic4s.legacy.ElasticDsl
import org.scalatest.FunSuite

class ClearCacheDslTest extends FunSuite with ElasticDsl {

  test("clear cache dsl compiles") {
    clear cache "myindex"
  }
}
