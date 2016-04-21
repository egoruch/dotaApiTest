package com.eb.schedule.live

import java.sql.Timestamp

import com.eb.schedule.dto._
import com.eb.schedule.model.MatchStatus
import com.eb.schedule.{HttpUtilsMock, RestBasicTest}
import org.json.{JSONArray, JSONObject}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 23.03.2016.
  */
class LiveGameProcessorTest extends RestBasicTest {

  val processor: LiveGameProcessor = createProcessor()
  val MATCH_ID: Long = 2234857740l

  private def createProcessor(): LiveGameProcessor = {
    new LiveGameProcessor(liveGameHelper, netWorthService, scheduledGameService, seriesService, new HttpUtilsMock)
  }

  test("getLiveLeagueGames") {
    val games: List[JSONObject] = processor.getLiveLeagueGames()
    assert(1 == games.size)
  }

  test("get absoluteNewGame") {
    processor.run()
    var cnt = 0
    var scheduledGameDTO: Option[ScheduledGameDTO] = None
    while (cnt < 4) {
      scheduledGameDTO = scheduledGameService.getScheduledGames(LiveGameContainer.getLiveGame(MATCH_ID).get, MatchStatus.LIVE)
      if (scheduledGameDTO.isDefined) {
        cnt = 5
      } else {
        Thread.sleep(3000)
        cnt = cnt + 1
      }
    }
    assert(scheduledGameDTO.isDefined, "failed to store new scheduled game")
    assert(scheduledGameDTO.get.matchStatus == MatchStatus.LIVE, "status of the game is wrong")
    assert(LiveGameContainer.exists(MATCH_ID))
  }

  test("scheduled game") {
    Await.result(scheduledGameService.insert(new ScheduledGameDTO(-1, new TeamDTO(36), new TeamDTO(1838315), new LeagueDTO(4210), new Timestamp(1l), MatchStatus.SCHEDULED)), Duration.Inf)
    LiveGameContainer.removeLiveGame(MATCH_ID)
    processor.run()
    var cnt = 0
    var scheduledGameDTO: Option[ScheduledGameDTO] = None
    while (cnt < 4) {
      scheduledGameDTO = scheduledGameService.getScheduledGames(LiveGameContainer.getLiveGame(MATCH_ID).get, MatchStatus.LIVE)
      if (scheduledGameDTO.isDefined) {
        cnt = 6
      } else {
        Thread.sleep(3000)
        cnt = cnt + 1
      }
    }
    assert(scheduledGameDTO.isDefined, "seems it couldn't find scheduled game by live game")
    assert(scheduledGameDTO.get.matchStatus == MatchStatus.LIVE, "failed to set LIVE status")
  }

  test("finish match") {
    LiveGameContainer.removeLiveGame(MATCH_ID)
    processor.run()
    Thread.sleep(3000)

    val currentMatch: CurrentGameDTO = LiveGameContainer.getLiveGame(MATCH_ID).get

    val emptyProcessor = new LiveGameProcessor(liveGameHelper, netWorthService, scheduledGameService, seriesService, new HttpUtilsMock() {
      override def getResponseAsJson(url: String): JSONObject = {
        val json: JSONObject = new JSONObject()
        val array: JSONArray = new JSONArray()
        json.put("games", array)
        val res: JSONObject = new JSONObject()
        res.put("result", json)
        res
      }
    })
    emptyProcessor.run()
    Thread.sleep(2000)
    assert(!LiveGameContainer.exists(MATCH_ID))
    val gameOpt: Option[ScheduledGameDTO] = scheduledGameService.getScheduledGames(currentMatch, MatchStatus.LIVE)
    assert(gameOpt.isDefined, "it is not the last game, so should be live status")

    new LiveGameProcessor(liveGameHelper, netWorthService, scheduledGameService, seriesService, new HttpUtilsMock() {
      override def getResponseAsJson(url: String): JSONObject = {
        val json: JSONObject = new JSONObject()
        val array: JSONArray = new JSONArray()
        array.put(getGame())
        json.put("games", array)
        val res: JSONObject = new JSONObject()
        res.put("result", json)
        res
      }
    }).run()
    Thread.sleep(2000)
    emptyProcessor.run()
    Thread.sleep(2000)
    assert(!LiveGameContainer.exists(MATCH_ID))
    val finishedMatch: Option[ScheduledGameDTO] = scheduledGameService.getScheduledGames(currentMatch, MatchStatus.FINISHED)
    assert(finishedMatch.isDefined)
    val series: Seq[SeriesDTO] = Await.result(seriesService.findBySeriesId(finishedMatch.get.id), Duration.Inf)
    assert(series.size == 2)
  }

}
