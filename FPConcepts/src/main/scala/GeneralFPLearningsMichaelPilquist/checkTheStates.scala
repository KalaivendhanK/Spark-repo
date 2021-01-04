package GeneralFPLearningsMichaelPilquist

//import GeneralFPLearningsMichaelPilquist.YextTimetrade.StateChangeCapability

object checkTheStates extends App {
  case class SafeRunAPI[Action](action: () ⇒ Action)

  val sr = SafeRunAPI(() ⇒ 1)
  val sr2: SafeRunAPI[Int] = SafeRunAPI[Int](() ⇒ sr.action() + 1)

  trait StateChangeCapability[F[_]] {
    def incrementByOne[Int](fa: F[Int]): F[Int]
  }
  implicit class ExtensionForTypeZeroOfIntTypes(fa: SafeRunAPI[Int]) {
    def increment(implicit S: StateChangeCapability[SafeRunAPI]): SafeRunAPI[Int] =
      S.incrementByOne(fa)
  }
  implicit def stateChangeCapabilityWithSafeRunAPI: StateChangeCapability[SafeRunAPI] =
    new StateChangeCapability[SafeRunAPI] {
      override def incrementByOne[Int](
          fa: SafeRunAPI[Int]
      ): SafeRunAPI[Int] =
        fa
    }
}
