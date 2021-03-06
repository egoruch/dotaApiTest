package com.eb.dotapulse.rest.http.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.eb.dotapulse.rest.data.{Data1, DataStorage}
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by Egor on 15.04.2017.
  */
class MatchRouteTest extends WordSpec with Matchers with ScalatestRouteTest {
  val route: Route = new MatchRoute().route

  "Get Match by Id " should {
    " be hanled" in {
      Get("/matches/123") ~> route ~> check {
        handled shouldBe true
      }
    }

    " not find any match by id" in {
      Get("/matches/123") ~> route ~> check {
        responseAs[String] shouldEqual ""
      }
    }

    "find match" in{
      val data = Data1("", Map("123" -> "MATCH"), Map())
      DataStorage.setData(data)
      Get("/matches/123") ~> route ~> check {
        responseAs[String] shouldEqual "MATCH"
      }
    }
  }

}
