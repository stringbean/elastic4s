package com.sksamuel.elastic4s.legacy.mappings

import org.elasticsearch.common.xcontent.XContentBuilder

case class TimestampDefinition(enabled: Boolean,
                               path: Option[String] = None,
                               format: Option[String] = None,
                               default: Option[String] = None,
                               store: Option[Boolean] = None,
                               docValuesFormat: Option[Boolean] = None) {

  def path(path: String): TimestampDefinition = copy(path = Option(path))
  def format(format: String): TimestampDefinition = copy(format = Option(format))
  def default(default: String): TimestampDefinition = copy(default = Option(default))
  def store(store: Boolean): TimestampDefinition = copy(store = Option(store))
  def docValuesFormat(b: Boolean): TimestampDefinition = copy(docValuesFormat = Option(b))

  private[elastic4s] def build(builder: XContentBuilder): Unit = {
    builder.startObject("_timestamp")
    builder.field("enabled", enabled)
    path.foreach(builder.field("path", _))
    store.foreach(s => builder.field("store", if (s) "yes" else "no"))
    format.foreach(builder.field("format", _))
    docValuesFormat.foreach(builder.field("doc_values", _))
    default.foreach(builder.field("default", _))
    builder.endObject()
  }
}
