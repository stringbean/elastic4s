package com.sksamuel.elastic4s.legacy

import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.client.{Client, Requests}
import org.elasticsearch.index.VersionType
import org.elasticsearch.search.fetch.source.FetchSourceContext

import scala.concurrent.Future
import scala.language.implicitConversions

/** @author Stephen Samuel */
trait GetDsl extends IndexesTypesDsl {

  class GetWithIdExpectsFrom(id: String) {
    def from(index: IndexesTypes): GetDefinition = new GetDefinition(index, id)
    def from(index: IndexType): GetDefinition = new GetDefinition(IndexesTypes(index), id)
    def from(index: String, `type`: String): GetDefinition = from(IndexesTypes(index, `type`))
    def from(index: String): GetDefinition = new GetDefinition(index, id)
  }

  implicit object GetDefinitionExecutable extends Executable[GetDefinition, GetResponse, GetResponse] {
    override def apply(c: Client, t: GetDefinition): Future[GetResponse] = {
      injectFuture(c.get(t.build, _))
    }
  }
}

case class GetDefinition(indexesTypes: IndexesTypes, id: String) {

  private val _builder = Requests.getRequest(indexesTypes.index).`type`(indexesTypes.typ.orNull).id(id)
  def build = _builder

  def fetchSourceContext(context: Boolean) = {
    _builder.fetchSourceContext(new FetchSourceContext(context))
    this
  }

  def fetchSourceContext(context: FetchSourceContext) = {
    _builder.fetchSourceContext(context)
    this
  }

  def fields(fs: String*): GetDefinition = fields(fs)
  def fields(fs: Iterable[String]): GetDefinition = {
    _builder.fields(fs.toSeq: _*)
    this
  }

  def ignoreErrorsOnGeneratedFields(ignoreErrorsOnGeneratedFields: Boolean) = {
    _builder.ignoreErrorsOnGeneratedFields(ignoreErrorsOnGeneratedFields)
    this
  }

  def parent(p: String) = {
    _builder.parent(p)
    this
  }

  def preference(pref: Preference): GetDefinition = preference(pref.elastic)
  def preference(pref: String): GetDefinition = {
    _builder.preference(pref)
    this
  }

  def realtime(r: Boolean) = {
    _builder.realtime(r)
    this
  }

  def refresh(refresh: Boolean) = {
    _builder.refresh(refresh)
    this
  }

  def routing(r: String) = {
    _builder.routing(r)
    this
  }

  def version(version: Long) = {
    _builder.version(version)
    this
  }

  def versionType(versionType: VersionType) = {
    _builder.versionType(versionType)
    this
  }
}


