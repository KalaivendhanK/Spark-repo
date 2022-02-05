package sparkSpace

import _root_.io.netty.util.internal.logging.{ InternalLoggerFactory, Slf4JLoggerFactory }
import org.apache.spark._
import org.apache.spark.sql.SparkSession
import org.scalatest.{ BeforeAndAfterAll, BeforeAndAfterEach, Suite }

/** Manages a local `sc` `SparkContext` variable, correctly stopping it after each test. */
trait LocalSparkContext extends BeforeAndAfterEach with BeforeAndAfterAll { self: Suite =>

  @transient var sc: SparkContext = _
  @transient var sparkSession: SparkSession = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE)
    sparkSession = SparkSession.builder().master("local").getOrCreate()
  }

  override def afterEach(): Unit = {
    try {
      resetSparkContext()
    }
    finally {
      super.afterEach()
    }
  }

  def resetSparkContext(): Unit = {
    LocalSparkContext.stop(sc)
    sc = null
    sparkSession.close()
  }

}

object LocalSparkContext {
  def stop(sc: SparkContext): Unit = {
    if (sc != null) {
      sc.stop()
    }
    // To avoid RPC rebinding to the same port, since it doesn't unbind immediately on shutdown
    System.clearProperty("spark.driver.port")
  }

  /** Runs `f` by passing in `sc` and ensures that `sc` is stopped. */
  def withSpark[T](sc: SparkContext)(f: SparkContext => T): T = {
    try {
      f(sc)
    }
    finally {
      stop(sc)
    }
  }

}