package com.home.zparkio.zparkio.services

import com.leobenkel.zparkio.Services.CommandLineArguments
import com.leobenkel.zparkio.Services.CommandLineArguments.CommandLineArguments
import com.leobenkel.zparkio.config.scallop.CommandLineArgumentScallop
import org.rogach.scallop.ScallopConf
import zio.{Task, ZIO}

case class Arguments(input: List[String])
    extends ScallopConf(input) with CommandLineArgumentScallop.Service[Arguments] {
}

object Arguments {
  def apply[A](f: Arguments => A): ZIO[CommandLineArguments[Arguments], Throwable, A] =
    CommandLineArguments.get[Arguments].apply(f)
}
