package com.mottalli.kotlintest

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.annotation.WebServlet
import java.util.logging.Logger
import java.util.logging.Level

import com.google.appengine.api.users.UserServiceFactory

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.DatastoreServiceFactory

@WebServlet(name="HelloWorldService", value="/")
class HelloWorldService : HttpServlet() {
    val datastoreService: DatastoreService = DatastoreServiceFactory.getDatastoreService()
    var id = 0
    val logger = Logger.getLogger(HelloWorldService::class.simpleName)

    init {
        logger.info("Initialized servlet")
    }

    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        val userService = UserServiceFactory.getUserService()!!

        logger.info("Got request: ${req?.requestURI}")
        logger.info("Current id is $id")

        val entity = Entity("test")
        entity.apply {
            setProperty("foo", "bar")
            setProperty("foo2", "baz")
            setProperty("id", id)
        }
        id = id.inc()

        datastoreService.put(entity)

        val msg: String = req?.userPrincipal?.name ?: "No user"


        resp?.apply {
            contentType = "text/plain"
            writer.println("Hello World from Kotlin! $msg")

            val q = Query("test")
            val pq = datastoreService.prepare(q)
            pq.asIterable().forEach {
                writer.println(it.properties["foo"])
                writer.println(it.key)
                writer.println(it.properties["id"])
                writer.println(userService.createLoginURL("/foo"))
            }
        }
    }
}