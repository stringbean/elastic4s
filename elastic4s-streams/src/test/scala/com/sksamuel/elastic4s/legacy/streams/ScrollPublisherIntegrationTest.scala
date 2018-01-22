package com.sksamuel.elastic4s.legacy.streams

import java.util.concurrent.{TimeUnit, CountDownLatch}

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.legacy.{RichSearchHit, IndexDefinition, ElasticDsl}
import com.sksamuel.elastic4s.legacy.jackson.ElasticJackson
import com.sksamuel.elastic4s.legacy.testkit.ElasticSugar
import org.reactivestreams.{Subscription, Subscriber}
import org.scalatest.{Matchers, WordSpec}

class ScrollPublisherIntegrationTest extends WordSpec with ElasticSugar with Matchers {

  import ElasticDsl._
  import ElasticJackson.Implicits._
  import ReactiveElastic._

  implicit val system = ActorSystem()

  val emperors = Array(
    Item("Augustus"),
    Item("Tiberius"),
    Item("Caligua"),
    Item("Claudius"),
    Item("Nero"),
    Item("Galba"),
    Item("Otho"),
    Item("Vitellius"),
    Item("Vespasian"),
    Item("Titus"),
    Item("Domitian"),
    Item("Nerva"),
    Item("Trajan"),
    Item("Hadrian"),
    Item("Antoninus Pius"),
    Item("Marcus Aurelius"),
    Item("Commodus"),
    Item("Pertinax"),
    Item("Diocletion")
  )

  implicit object RichSearchHitRequestBuilder$ extends RequestBuilder[RichSearchHit] {
    override def request(hit: RichSearchHit): IndexDefinition = {
      index into "scrollpubint" / "emperor" source hit.sourceAsString
    }
  }

  ensureIndexExists("scrollpubint")

  client.execute {
    bulk(emperors.map(index into "scrollpubint" / "emperors" source _))
  }.await

  blockUntilCount(emperors.length, "scrollpubint")

  "elastic-streams" should {
    "publish all data from the index" in {

      val publisher = client.publisher(search in "scrollpubint" / "emperors" query "*:*" scroll "1m")

      val completionLatch = new CountDownLatch(1)
      val documentLatch = new CountDownLatch(emperors.length)

      publisher.subscribe(new Subscriber[RichSearchHit] {
        override def onComplete(): Unit = completionLatch.countDown()
        override def onError(t: Throwable): Unit = fail(t)
        override def onSubscribe(s: Subscription): Unit = s.request(1000)
        override def onNext(t: RichSearchHit): Unit = documentLatch.countDown()
      })
      client

      completionLatch.await(5, TimeUnit.SECONDS) should be (true)
      documentLatch.await(5, TimeUnit.SECONDS) should be (true)
    }
  }
}
