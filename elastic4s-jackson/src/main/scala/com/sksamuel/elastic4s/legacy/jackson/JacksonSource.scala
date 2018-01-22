package com.sksamuel.elastic4s.legacy.jackson

import com.fasterxml.jackson.databind.JsonNode
import com.sksamuel.elastic4s.legacy.source.DocumentSource

/** @author Stephen Samuel */
@deprecated("Prefer Indexable[T] typeclass", "1.5.12")
class JacksonSource(root: JsonNode) extends DocumentSource {
  def json = root.toString
}

object JacksonSource {
  @deprecated("Prefer Indexable[T] typeclass", "1.5.12")
  def apply(root: JsonNode) = new JacksonSource(root)
}
