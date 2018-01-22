package com.sksamuel.elastic4s.legacy.examples

import com.sksamuel.elastic4s.legacy.ElasticDsl

// examples of the count API in dot notation
class ValidateDotDsl extends ElasticDsl {

  // simple query being validated
  validateIn("index" / "type").query("id:123")

  // validating boolean query
  validateIn("index" / "type").query(
    bool {
      must {
        termQuery("name", "sammy")
      } should {
        termQuery("place", "buckinghamshire")
      }
    }
  )
}


