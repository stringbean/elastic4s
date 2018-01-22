package com.sksamuel.elastic4s.legacy.streams

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.legacy.jackson.ElasticJackson
import com.sksamuel.elastic4s.legacy.testkit.ElasticSugar
import com.sksamuel.elastic4s.legacy.{BulkCompatibleDefinition, ElasticDsl}
import org.reactivestreams.tck.SubscriberWhiteboxVerification.{SubscriberPuppet, WhiteboxSubscriberProbe}
import org.reactivestreams.tck.{SubscriberWhiteboxVerification, TestEnvironment}
import org.reactivestreams.{Subscriber, Subscription}
import org.scalatest.testng.TestNGSuiteLike

class BulkIndexingSubscriberWhiteboxTest
  extends SubscriberWhiteboxVerification[Item](new TestEnvironment(DEFAULT_TIMEOUT_MILLIS))
  with ElasticSugar with TestNGSuiteLike {

  import ElasticDsl._

  implicit val system = ActorSystem()

  try {
    client.execute {
      create index "bulkindexwhitebox"
    }.await
  } catch {
    case e: Exception =>
  }

  object ItemRequestBuilder extends RequestBuilder[Item] {

    import ElasticJackson.Implicits._

    override def request(t: Item): BulkCompatibleDefinition = index into "bulkindexwhitebox" / "castles" source t
  }

  override def createSubscriber(probe: WhiteboxSubscriberProbe[Item]): Subscriber[Item] = {
    new BulkIndexingSubscriber[Item](
      client,
      ItemRequestBuilder,
      ResponseListener.noop,
      10,
      2,
      false,
      () => (),
      _ => (),
      None) {

      override def onSubscribe(s: Subscription): Unit = {
        super.onSubscribe(s)
        // register a successful Subscription, and create a Puppet,
        // for the WhiteboxVerification to be able to drive its tests:
        probe.registerOnSubscribe(new SubscriberPuppet() {

          def triggerRequest(elements: Long): Unit = {
            s.request(elements)
          }

          def signalCancel(): Unit = {
            s.cancel()
          }
        })
      }

      override def onComplete(): Unit = {
        super.onComplete()
        probe.registerOnComplete()
      }

      override def onError(t: Throwable): Unit = {
        probe.registerOnError(t)
      }

      override def onNext(t: Item): Unit = {
        super.onNext(t)
        probe.registerOnNext(t)
      }
    }
  }

  override def createElement(element: Int): Item = castles(element)

  val castles = Array(
    Item("bodium"),
    Item("hever"),
    Item("tower of london"),
    Item("canarvon"),
    Item("conwy"),
    Item("beaumaris"),
    Item("bolsover"),
    Item("conningsbrough"),
    Item("tintagel"),
    Item("rochester"),
    Item("dover"),
    Item("hexham"),
    Item("harleigh"),
    Item("white"),
    Item("radley"),
    Item("berkeley")
  )
}

case class Item(name: String)


