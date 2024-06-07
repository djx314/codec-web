package pet.generic

trait SlickNamed[F[_[_]]] {
  def labelled: F[DtoNamed.Named]
}

object SlickNamed {

  type Named[_] = String

  class DerivedApply[F[_[_]]] {
    def from(m: F[Named]): SlickNamed[F] =
      new SlickNamed[F] {
        override val labelled: F[Named] = m
      }
  }

  def apply[F[_[_]]]: DerivedApply[F] = new DerivedApply[F]

}
