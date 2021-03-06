package com.eb.pulse.crawler.data.task

import com.eb.pulse.crawler.CrawlerUrls
import com.eb.pulse.crawler.data.service.MatchService
import com.eb.schedule.model.SeriesType
import com.eb.schedule.model.slick.{MatchSeries, ScheduledGame}
import com.eb.schedule.utils.HttpUtils
import com.google.gson.JsonObject
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FindWinnerForTheMatchTask(matchService: MatchService, httpUtils: HttpUtils) extends Runnable{

  private val log = LoggerFactory.getLogger(this.getClass)

  override def run(): Unit = {
    try {
      val unfinishedGames: Future[Map[ScheduledGame, Seq[MatchSeries]]] = matchService.getNotFinishedGamesWithMatches()
      unfinishedGames.map(games => games.foreach(tuple => tuple._2.foreach(updateWinners(_, tuple._1))))
    } catch {
      case e: Throwable => log.error("Issue in running task", e)
    }
  }

  def updateWinners(series: MatchSeries, game: ScheduledGame): Unit = {
    if (series.radiantWin.isEmpty) {
      val response: JsonObject = httpUtils.getResponseAsJson(CrawlerUrls.GET_MATCH_DETAILS + series.matchId)
      val result: JsonObject = response.getAsJsonObject("result")
      if (result != null && !result.has("error")) {
        try {
          val radiantWin: Boolean = result.get("radiant_win").getAsBoolean
          if (SeriesType.BO1.code == game.seriesType) {
            matchService.updateMatchWithWinner(series.matchId, radiantWin)
          } else {
            if (result.has("radiant_team_id")) {
              if (result.get("radiant_team_id").getAsInt == game.radiant) {
                matchService.updateMatchWithWinner(series.matchId, radiantWin)
              } else {
                matchService.updateMatchWithWinner(series.matchId, !radiantWin)
              }
            } else {
              if (series.radiantTeam == game.radiant) {
                matchService.updateMatchWithWinner(series.matchId, radiantWin)
              } else {
                matchService.updateMatchWithWinner(series.matchId, !radiantWin)
              }
            }
          }
          log.debug("Winner updated for matchId: {}", series)
        } catch {
          case e: Throwable => log.error("Couldn't update winner for match: " + series, e)
        }
      }
    }
  }
}
