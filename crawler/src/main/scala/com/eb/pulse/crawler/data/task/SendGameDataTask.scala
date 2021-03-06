package com.eb.pulse.crawler.data.task

import com.eb.pulse.crawler.data.cache.CacheHelper
import com.eb.pulse.crawler.data.model.{Data, GameDataHolder, LiveMatch}
import com.eb.pulse.crawler.data.service.{GameService, MatchService}
import com.eb.pulse.crawler.data.transformer.LiveMatchTransformer
import com.eb.schedule.model.SeriesType
import com.eb.schedule.model.slick.{League, MatchSeries, ScheduledGame, Team}
import com.eb.schedule.shared.bean.{GameBean, LeagueBean, Match, TeamBean}
import com.eb.schedule.utils.HttpUtils
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import org.slf4j.LoggerFactory

import scala.collection.immutable.{HashMap, ListMap}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by Iegor.Bondarenko on 01.05.2017.
  */
class SendGameDataTask(liveMatches: Seq[LiveMatch], gameService: GameService, matchService: MatchService, httpUtils: HttpUtils, cacheHelper: CacheHelper) {
  private val log = LoggerFactory.getLogger(this.getClass)

  val formatter = DateTimeFormat.forPattern("EEEE, d MMM")

  val liveMatchesById = liveMatches.map(m => m.matchId -> m).toMap


  def execute(): Unit = {
    val gamesBetweenDate: Future[Map[ScheduledGame, Seq[MatchSeries]]] = gameService.getRecentGames(System.currentTimeMillis())
    gamesBetweenDate.onComplete {
      case Success(gamesPair) => {
        val currentGames = processGames(gamesPair)
        val currentMatches: Map[String, Match] = processMatch(gamesPair.values.flatten)

        var matchesByGames = new HashMap[String, Seq[String]]()
        for ((game, mathces) <- gamesPair) {

          var matchesByGameNumber = HashMap[String, String]()
          for (m <- mathces.sortBy(_.startDate.getTime)) {
            val matchBean = currentMatches(m.matchId.toString)
            matchesByGameNumber = matchesByGameNumber + (m.gameNumber.toString -> matchBean.getMatchId.toString)
          }
          val matchesList = matchesByGameNumber.toSeq.sortBy(_._1).unzip._2
          matchesByGames += (game.id.toString -> matchesList)
        }

        GameDataHolder.gatheredData = Data(currentGames, currentMatches, matchesByGames)

      }
      case Failure(ex) => log.error("Couldn't get current games", ex)

    }
  }

  def processGames(gamesPair: Map[ScheduledGame, Seq[MatchSeries]]): Map[String, List[GameBean]] = {
    var currentGames: List[GameBean] = Nil
    for ((game, matches) <- gamesPair) {
      val finishedMatches = matches.filter(_.radiantWin.isDefined)
      val gameBean = new GameBean()
      fillTeamAndLeague(gameBean, game)
      fillWithScore(gameBean, finishedMatches)
      gameBean.setId(game.id)
      gameBean.setSeriesType(SeriesType.fromCode(game.seriesType).name())
      gameBean.setGameStatus(game.status)
      gameBean.setNumberOfGames(matches.map(_.gameNumber).toSet.size)
      gameBean.setStartTime(game.startDate.getTime)

      currentGames = gameBean :: currentGames
    }
    val currentGamesByMillis: Map[Long, List[GameBean]] = currentGames.groupBy(g => getMillisInUTC(g.startTime))
    val sortedCurrentGameByMillis: ListMap[Long, List[GameBean]] = ListMap(currentGamesByMillis.toSeq.sortWith(_._1 > _._1): _*)
    sortedCurrentGameByMillis.map { case (k, v) => formatter.print(k) -> v.sortBy(g => (g.gameStatus, -g.startTime)) }

  }

  def fillTeamAndLeague(gameBean: GameBean, game: ScheduledGame): Unit = {
    val radiantTeam: Team = cacheHelper.getTeam(game.radiant)
    val direTeam: Team = cacheHelper.getTeam(game.dire)
    val league: League = cacheHelper.getLeague(game.leagueId)
    gameBean.setRadiant(new TeamBean(radiantTeam.id, radiantTeam.name, radiantTeam.tag, radiantTeam.logo))
    gameBean.setDire(new TeamBean(direTeam.id, direTeam.name, direTeam.tag, direTeam.logo))
    gameBean.setLeague(new LeagueBean(league.id, league.name))
  }

  def processMatch(storedMatches: Iterable[MatchSeries]): Map[String, Match] = {
    var allMatchesById: Map[String, Match] = new HashMap[String, Match]
    for (m <- storedMatches) {
      val liveMatchOption = liveMatchesById.get(m.matchId)
      liveMatchOption match {
        case Some(m) =>
          val matchBean = LiveMatchTransformer.transform(m)
          allMatchesById = allMatchesById + (m.matchId.toString -> matchBean)
        case None =>
          cacheHelper.getMatch(m.matchId) match {
            case Some(x) => allMatchesById += (m.matchId.toString -> x)
            case None => None
          }
      }
    }

    allMatchesById
  }


  def fillWithScore(gameBean: GameBean, matches: Seq[MatchSeries]) = {
    var matchesByGameNumber = HashMap[Byte, MatchSeries]()
    for (m <- matches.sortBy(_.startDate.getTime)) {
      matchesByGameNumber = matchesByGameNumber + (m.gameNumber -> m)
    }
    matchesByGameNumber.values.foreach(matches =>
      if (matches.radiantWin.get) gameBean.setRadiantWin(gameBean.radiantWin + 1) else gameBean.setDireWin(gameBean.direWin + 1))
  }

  def getMillisInUTC(mil: Long): Long = {
    new DateTime().withMillis(mil).withZone(DateTimeZone.UTC).withTimeAtStartOfDay().getMillis
  }
}
