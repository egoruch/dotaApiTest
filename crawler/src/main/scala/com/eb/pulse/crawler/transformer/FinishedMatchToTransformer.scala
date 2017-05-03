package com.eb.pulse.crawler.transformer

import com.eb.pulse.crawler.Lookup
import com.eb.pulse.crawler.cache.CacheHelper
import com.eb.pulse.crawler.model.{FinishedMatch, Player, TeamScoreBoard}
import com.eb.schedule.model.MatchStatus
import com.eb.schedule.shared.bean.{HeroBean, Item, Match, TeamBean}

import scala.collection.JavaConverters._


/**
  * Created by Iegor.Bondarenko on 01.05.2017.
  */
object FinishedMatchToTransformer {

  val cacheHelper: CacheHelper = Lookup.cacheHelper

  def transform(finishedMatch: FinishedMatch): Match = {
    val radiantTeam = transformTeam(finishedMatch.radiantTeam)
    val direTeam = transformTeam(finishedMatch.direTeam)
    val matchBean = new Match()
    matchBean.setMatchId(finishedMatch.matchId)
    //todo do i need start time
    matchBean.setStartTime(finishedMatch.startTime)
    matchBean.setDuration(getDuration(finishedMatch.duration))
    matchBean.setMatchStatus(MatchStatus.FINISHED.status)
    matchBean.setRadiantTeam(radiantTeam)
    matchBean.setDireTeam(direTeam)
    matchBean.setNetworth(seqAsJavaList(finishedMatch.netWorth.netWorth.split(",").toList.map(new Integer(_))))
    matchBean.setGameNumber(-1) //todo do i need gameNumber
    matchBean.setRadianPicks(seqAsJavaList(finishedMatch.radiantTeam.picks.map(cacheHelper.getHero(_)).map(hero => new HeroBean(hero.id, hero.name))))
    matchBean.setRadianBans(seqAsJavaList(finishedMatch.radiantTeam.bans.map(cacheHelper.getHero(_)).map(hero => new HeroBean(hero.id, hero.name))))
    matchBean.setDirePicks(seqAsJavaList(finishedMatch.direTeam.picks.map(cacheHelper.getHero(_)).map(hero => new HeroBean(hero.id, hero.name))))
    matchBean.setDireBans(seqAsJavaList(finishedMatch.direTeam.bans.map(cacheHelper.getHero(_)).map(hero => new HeroBean(hero.id, hero.name))))
    matchBean
  }


  def transformTeam(teamScoreBoard: TeamScoreBoard): TeamBean = {
    val players = transformPlayers(teamScoreBoard.players)
    val team = teamScoreBoard.team
    new TeamBean(team.id, team.name, team.tag, team.logo, seqAsJavaList(players))
  }

  def transformPlayers(players: List[Player]): Seq[com.eb.schedule.shared.bean.Player] = {
    players.map(p => {
      val hero = cacheHelper.getHero(p.hero)
      val items = p.items.map(cacheHelper.getItem).map(item => new Item(item.id, item.name))

      val player = new com.eb.schedule.shared.bean.Player()
      player.setAccountId(p.accountId)
      player.setName(cacheHelper.getPlayerName(p.accountId))
      player.setHero(new HeroBean(hero.id, hero.name))
      player.setItems(seqAsJavaList(items))
      player.setLevel(p.level)
      player.setKills(p.kills)
      player.setDeaths(p.death)
      player.setAssists(p.assists)
      player.setNetWorth(p.netWorth)
      player
    })
  }

  def getDuration(duration:Int) = {
    val minutes: Int = duration / 60
    val seconds: Int = duration - minutes * 60
    minutes + ":" + "%02d".format(seconds)
  }

}