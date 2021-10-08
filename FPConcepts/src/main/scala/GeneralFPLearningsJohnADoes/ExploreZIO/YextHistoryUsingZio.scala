package GeneralFPLearningsJohnADoes.ExploreZIO

import zio.{ Has, IO, ZLayer }

object YextHistoryUsingZio {

  case class User(name: String, age: Int)
  case class UserId(id: BigInt)
  type UserRepo = Has[UserRepo.Service]

  object UserRepo {
    trait Service {
      def getUser(userId: UserId): IO[Throwable, Option[User]]
      def createUser(user: User): IO[Throwable, Unit]
    }

    val testRepo: ZLayer[Any, Nothing, UserRepo] = ZLayer.succeed(???)
  }
}
