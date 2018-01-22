package com.sksamuel.elastic4s.legacy

trait ElasticImplicits {
  implicit class RichString(str: String) {
    def /(`type`: String): IndexType = IndexType(str, `type`)
  }
}

object ElasticImplicits extends ElasticImplicits
