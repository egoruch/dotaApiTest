package com.eb.schedule.model.services

import com.eb.schedule.model.dao.UpdateTaskRepository
import com.eb.schedule.model.slick.UpdateTask
import com.google.inject.Inject

import scala.concurrent.Future

/**
  * Created by Egor on 20.02.2016.
  */
trait UpdateTaskService {
  def findByIdAndName(id: Long, classname: String): Future[UpdateTask]

  def exists(id: Long, classname: String): Future[Boolean]

  def insert(task: UpdateTask): Future[Int]

  def update(task: UpdateTask): Future[Int]

  def delete(id: Long, classname: String): Future[Int]

  def getPendingTasks(classname: String): Future[Seq[UpdateTask]]
}

class UpdateTaskServiceImpl @Inject()(taskRepository: UpdateTaskRepository) extends UpdateTaskService {
  def findByIdAndName(id: Long, classname: String): Future[UpdateTask] = {
    taskRepository.findByIdAndName(id, classname)
  }

  def exists(id: Long, classname: String): Future[Boolean] = {
    taskRepository.exists(id, classname)
  }

  def insert(task: UpdateTask): Future[Int] = {
    taskRepository.insert(task)
  }

  def update(task: UpdateTask): Future[Int] = {
    taskRepository.update(task)
  }

  def delete(id: Long, classname: String): Future[Int] = {
    taskRepository.delete(id, classname)
  }

  def getPendingTasks(classname: String): Future[Seq[UpdateTask]] = {
    taskRepository.getPendingTasks(classname)
  }
}