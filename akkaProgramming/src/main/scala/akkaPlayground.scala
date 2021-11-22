import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

object akkaPlayground extends App{

  object SimpleActorClass {
    case class SendMessageBack(message: String)
    case class PrintMessage(message: String)
  }

  object SimpleChildActorClass {
    case class ChildPrint(childMessage: String)
    case class SendToChild(message: String)
  }

  import SimpleActorClass._
  import SimpleChildActorClass._

  class SimpleActorClass extends Actor with ActorLogging {
    override def receive: Receive = {
      case SendMessageBack(message) => sender() ! message
      case PrintMessage(message) => println(s"I have received a ${message}")
      case SendToChild(message) =>
        val child = context.actorOf(Props[SimpleChildActorClass],"child")
        child ! ChildPrint(message)
    }
  }
  class SimpleChildActorClass extends Actor with ActorLogging {
    override def receive: Receive = {
      case ChildPrint(childMessage) => println(s"Printing the message from child: ${childMessage}")
    }
  }
  val system = ActorSystem("simpleSystem")
  val testActor = system.actorOf(Props[SimpleActorClass],"simpleActor")

  testActor ! SendMessageBack("test")
  testActor ! PrintMessage("test")
  testActor ! SendToChild("child test")

}
