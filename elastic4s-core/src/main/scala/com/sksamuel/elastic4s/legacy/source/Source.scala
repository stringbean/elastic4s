package com.sksamuel.elastic4s.legacy.source

/** @author Stephen Samuel */
trait DocumentSource {
  def json: String
}

@deprecated("prefer JsonDocumentSource instead; same semantics just different name", "1.5.0")
case class StringDocumentSource(str: String) extends DocumentSource {
  override def json = str
}

/** An instance of DocumentSource that just provides json as is
  */
case class JsonDocumentSource(j: String) extends DocumentSource {
  override def json = j
}

trait DocumentMap {
  def map: Map[String, Any]
}

