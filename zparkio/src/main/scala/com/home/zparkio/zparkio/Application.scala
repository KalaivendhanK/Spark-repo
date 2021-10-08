package com.home.zparkio.zparkio

import com.leobenkel.zparkio.Services._
import com.leobenkel.zparkio.Services.Logger.Logger
import com.leobenkel.zparkio.ZparkioApp
import com.home.zparkio.zparkio.Application._
import com.home.zparkio.zparkio.services._
import com.leobenkel.zparkio.config.scallop.CommandLineArgumentScallop
import izumi.reflect.Tag
import zio.{ Has, Task, ZIO, ZLayer }

trait Application extends ZparkioApp[Arguments, RuntimeEnv, OutputType] {
  implicit lazy final override val tagC: Tag[Arguments] = Tag.tagFromTagMacro
  implicit lazy final override val tagEnv: Tag[RuntimeEnv] = Tag.tagFromTagMacro

  // To add new services
  lazy final override protected val env: ZLayer[ZPARKIO_ENV, Throwable, RuntimeEnv] =
    ZLayer.succeed(())

  override protected def sparkFactory: FACTORY_SPARK = SparkBuilder
  lazy final override protected val loggerFactory: FACTORY_LOG = Logger.Factory(Log)

  lazy final override protected val cliFactory: FACTORY_CLI =
    CommandLineArgumentScallop.Factory[Arguments]()
  lazy final override protected val makeConfigErrorParser: ERROR_HANDLER =
    CommandLineArgumentScallop.ErrorParser
  override protected def makeCli(args: List[String]): Arguments = Arguments(args)

  // Where the core of your application goes
  override def runApp(): ZIO[COMPLETE_ENV, Throwable, OutputType] = {
    for {
      _ <- Logger.info("Start ZparkIO")
      _ <- Logger.info("Completed ZparkIO")
    } yield (())
  }
}

object Application {
  // To add new services
  type RuntimeEnv = Has[Unit]
  // To change output type
  type OutputType = Unit
}
