package com.sksamuel.elastic4s

import org.elasticsearch.index.VersionType
import org.elasticsearch.action.index.IndexRequest.OpType
import org.elasticsearch.common.xcontent.{XContentFactory, XContentBuilder}
import org.elasticsearch.action.index.{IndexAction, IndexRequest}
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._
import com.sksamuel.elastic4s.source.{DocumentMap, DocumentSource, Source}
import java.{util => ju}

/** @author Stephen Samuel */
trait IndexDsl {

  def insert: IndexExpectsInto = index
  def index: IndexExpectsInto = new IndexExpectsInto

  class IndexExpectsInto {
    def into(index: String): IndexDefinition = into(index.split("/").head, index.split("/").last)
    def into(index: String, `type`: String): IndexDefinition = new IndexDefinition(index, `type`)
    def into(kv: (String, String)): IndexDefinition = into(kv._1, kv._2)
  }

  class IndexDefinition(index: String, `type`: String)
      extends RequestDefinition(IndexAction.INSTANCE) with BulkCompatibleDefinition {

    private val _request = new IndexRequest(index, `type`)
    private val _fields = new ListBuffer[(String, Any)]
    private var _source: Option[DocumentSource] = None
    private var _map: Option[DocumentMap] = None

    def build = _source match {
      case Some(src) => _request.source(src.json)
      case None => _map match {
        case Some(map) => _request.source(map.map.asJava)
        case None => _request.source(_fieldsAsXContent)
      }
    }

    def _fieldsAsXContent: XContentBuilder = {
      val source = XContentFactory.jsonBuilder()

      val converted = prepareFields(_fields.toMap)
      source.map(converted)
    }

    def id(id: Any): IndexDefinition = {
      _request.id(id.toString)
      this
    }
    def routing(routing: String): IndexDefinition = {
      _request.routing(routing)
      this
    }

    def parent(parent: String): IndexDefinition = {
      _request.parent(parent)
      this
    }

    def timestamp(timestamp: String): IndexDefinition = {
      _request.timestamp(timestamp)
      this
    }

    def ttl(ttl: Long): IndexDefinition = {
      _request.ttl(ttl)
      this
    }

    def update(update: Boolean): IndexDefinition = opType(OpType.CREATE)
    def opType(opType: IndexRequest.OpType): IndexDefinition = {
      _request.opType(opType)
      this
    }

    def version(version: Int): IndexDefinition = {
      _request.version(version)
      this
    }

    def versionType(versionType: VersionType): IndexDefinition = {
      _request.versionType(versionType)
      this
    }

    def fields(map: Map[String, Any]): IndexDefinition = fields(map.toList)
    def fields(_fields: (String, Any)*): IndexDefinition = fields(_fields.toIterable)
    def fields(iterable: Iterable[(String, Any)]): IndexDefinition = {
      this._fields ++= iterable
      this
    }

    def doc(source: DocumentSource) = {
      this._source = Option(source)
      this
    }

    def doc(map: DocumentMap) = {
      this._map = Option(map)
      this
    }

    @deprecated("renamed to doc", "1.0")
    def source(source: Source) = doc(source)

    private def prepareFields(fields: Map[String, Any]): ju.Map[String, AnyRef] = {
      fields.map {
        case (k: String, v: Array[Map[_, _]]) =>
          (k, v.map(_.asJava))
        case (k: String, v: Array[Array[(_, _)]]) =>
          (k, v.map(_.toMap.asJava))
        case (k: String, v: Array[Seq[(_, _)]]) =>
          (k, v.map(ve => prepareFields(ve.toMap.asInstanceOf[Map[String, Any]])))
        case (k: String, v: Map[_, _]) =>
          (k, prepareFields(v.asInstanceOf[Map[String, Any]]))
        case (k: String, v: Array[_]) =>
          (k, v)
        case (k: String, null) =>
          (k, null)
        case (k: String, v: Any) =>
          (k, v)
      }.asJava.asInstanceOf[ju.Map[String, AnyRef]]
    }
  }
}
