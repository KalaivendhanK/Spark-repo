package sparkTestSpace

import _root_.io.netty.util.internal.logging.{ InternalLoggerFactory, Slf4JLoggerFactory }
import org.apache.spark._
import org.apache.spark.sql.SparkSession
import org.scalatest.{ BeforeAndAfterAll, BeforeAndAfterEach, Suite }

/** Manages a local `sc` `SparkContext` variable, correctly stopping it after each test. */
trait LocalSparkContext extends BeforeAndAfterEach with BeforeAndAfterAll { self: Suite =>

  //  @transient var sc: SparkContext = _
  @transient var sparkSession: SparkSession = _

  override def beforeAll(): Unit = {
    sparkSession = SparkSession.builder().master("local").getOrCreate()
  }

  override def afterEach(): Unit = {
    super.afterEach()
    sparkSession.close()
  }

  //  def resetSparkContext(): Unit = {
  //    LocalSparkContext.stop(sc)
  //    sc = null
  //    sparkSession.close()
  //  }

}
