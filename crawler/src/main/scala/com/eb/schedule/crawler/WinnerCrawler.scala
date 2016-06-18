package com.eb.schedule.crawler

import com.eb.schedule.dto.{ScheduledGameDTO, SeriesDTO}
import com.eb.schedule.model.SeriesType
import com.eb.schedule.model.services.ScheduledGameService
import com.eb.schedule.model.slick.MatchSeries
import com.eb.schedule.services.SeriesService
import com.eb.schedule.utils.HttpUtils
import com.google.gson.JsonObject
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class WinnerCrawler @Inject()(seriesService: SeriesService, scheduledGameService: ScheduledGameService, httpUtils: HttpUtils) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  override def run(): Unit = {
    try {
      val series1: Map[ScheduledGameDTO, Seq[SeriesDTO]] = seriesService.getUnfinishedSeries()
      series1.foreach(tuple => tuple._2.foreach(updateWinners(_, tuple._1)))
    } catch {
      case e: Throwable => log.error("", e)
    }
  }

  def updateWinners(series: SeriesDTO, game: ScheduledGameDTO): Unit = {
    if (series.radiantWin.isEmpty) {
      val response: JsonObject = httpUtils.getResponseAsJson(CrawlerUrls.GET_MATCH_DETAILS + series.matchId)
      val result: JsonObject = response.getAsJsonObject("result")
      if (result != null && !result.has("error")) {
        val radiantWin: Boolean = result.get("radiant_win").getAsBoolean
        if (SeriesType.NO_SERIES == game.seriesType) {
          series.radiantWin = Some(radiantWin)
        } else {
          if (result.has("radiant_team_id")) {
            if (result.get("radiant_team_id").getAsInt == game.radiantTeam.id) {
              series.radiantWin = Some(radiantWin)
            } else {
              series.radiantWin = Some(!radiantWin)
            }
          } else {
            if (series.radiantTeamId == game.radiantTeam.id) {
              series.radiantWin = Some(radiantWin)
            } else {
              series.radiantWin = Some(!radiantWin)
            }
          }
        }
        seriesService.update(series)
        log.debug("Winner updated for matchId: " + series.matchId + " and seriesId: " + series.gameId)
      }
    }
  }
}
