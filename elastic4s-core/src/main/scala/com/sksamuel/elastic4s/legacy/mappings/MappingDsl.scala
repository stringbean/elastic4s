package com.sksamuel.elastic4s.legacy.mappings

import com.sksamuel.elastic4s.legacy.{IndexType, Executable, ProxyClients}
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.admin.indices.mapping.put.{PutMappingRequest, PutMappingRequestBuilder, PutMappingResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future
import scala.language.implicitConversions

/** @author Stephen Samuel */
trait MappingDsl {

  val NotAnalyzed: String = "not_analyzed"
  def id: FieldDefinition = "_id"

  implicit def stringToField(name: String): FieldDefinition = new FieldDefinition(name)
  implicit def stringToMap(`type`: String): MappingDefinition = new MappingDefinition(`type`)

  implicit object GetMappingDefinitionExecutable
    extends Executable[GetMappingDefinition, GetMappingsResponse, GetMappingsResponse] {
    override def apply(c: Client, t: GetMappingDefinition): Future[GetMappingsResponse] = {
      injectFuture(c.admin.indices.prepareGetMappings(t.indexes.toSeq: _*).setTypes(t.types.toSeq: _*).execute)
    }
  }

  implicit object PutMappingDefinitionExecutable
    extends Executable[PutMappingDefinition, PutMappingResponse, PutMappingResponse] {
    override def apply(c: Client, t: PutMappingDefinition): Future[PutMappingResponse] = {
      injectFuture(c.admin.indices.putMapping(t.request, _))
    }
  }

  implicit object DeleteMappingDefinitionExecutable
    extends Executable[DeleteMappingDefinition, DeleteMappingResponse, DeleteMappingResponse] {
    override def apply(c: Client, t: DeleteMappingDefinition): Future[DeleteMappingResponse] = {
      injectFuture(c.admin.indices.prepareDeleteMapping(t.indexes.toSeq: _*).setType(t.types.toSeq: _*).execute)
    }
  }
}

class PutMappingDefinition(indexType: IndexType) extends MappingDefinition(indexType.`type`) {

  var ignore: Option[Boolean] = None

  def ignoreConflicts(ignore: Boolean): this.type = {
    this.ignore = Option(ignore)
    this
  }

  def request: PutMappingRequest = {
    val req = new PutMappingRequestBuilder(ProxyClients.indices)
      .setIndices(indexType.index)
      .setType(`type`)
      .setSource(super.build)
    ignore.foreach(req.setIgnoreConflicts)
    req.request()
  }
}
