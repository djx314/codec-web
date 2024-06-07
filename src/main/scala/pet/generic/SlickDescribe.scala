package pet.generic

trait SlickDescribe[F[_[_]]] {
  def labelled: F[DtoNamed.Named]
}

object SlickDescribe {

  type Named[_] = String

  class DerivedApply[F[_[_]]] {
    def from(m: F[Named]): SlickDescribe[F] =
      new SlickDescribe[F] {
        override val labelled: F[Named] = m
      }
  }

  def apply[F[_[_]]]: DerivedApply[F] = new DerivedApply[F]

}
