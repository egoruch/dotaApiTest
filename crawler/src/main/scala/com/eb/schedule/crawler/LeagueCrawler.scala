package com.eb.schedule.crawler

import com.eb.schedule.crawler.CrawlerUrls._
import com.eb.schedule.dto.{LeagueDTO, TaskDTO}
import com.eb.schedule.model.Finished
import com.eb.schedule.model.services.{LeagueService, UpdateTaskService}
import com.eb.schedule.model.slick.{League, UpdateTask}
import com.eb.schedule.utils.HttpUtils
import com.google.inject.Inject
import org.json.{JSONArray, JSONObject}
import org.slf4j.LoggerFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class LeagueCrawler @Inject()(leagueService: LeagueService, taskService: UpdateTaskService, httpUtils: HttpUtils) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  def run() {
    val tasks: Future[Seq[TaskDTO]] = taskService.getPendingLeagueTasks()
    val result: Seq[TaskDTO] = Await.result(tasks, Duration.Inf)
    val ids: Seq[Long] = result.map(_.id)
    val steamItems: JSONArray = getItemsInfoFromSteam()
    for (i <- 0 until steamItems.length()) {
      val itemJson: JSONObject = steamItems.getJSONObject(i)

      val leagueId: Int = itemJson.getInt("leagueid")
      if (ids.contains(leagueId)) {
        log.info("find league to process: " + leagueId)
        leagueService.insert(new LeagueDTO(leagueId, parseName(itemJson.getString("name")), itemJson.getString("tournament_url")))
        taskService.update(new UpdateTask(leagueId, League.getClass.getSimpleName, Finished.status.toByte))
      }
    }
  }


  def parseName(name: String): String = {
    name
    //    name.replace("item_", "").replace("_", " ")
  }


  def getItemsInfoFromSteam(): JSONArray = {
    val teamInfo: JSONObject = httpUtils.getResponseAsJson(GET_LEAGUES)
    val result: JSONObject = teamInfo.getJSONObject("result")
    val items: JSONArray = result.getJSONArray("leagues")
    items
  }

}
