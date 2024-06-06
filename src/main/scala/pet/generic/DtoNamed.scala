package pet.generic

trait DtoNamed[F[_[_]]] {
  def labelled: F[DtoNamed.Named]
}

object DtoNamed {

  type Named[_] = String

  class DerivedApply[F[_[_]]] {
    def from(m: F[Named]): DtoNamed[F] =
      new DtoNamed[F] {
        override val labelled: F[Named] = m
      }
  }

  def apply[F[_[_]]]: DerivedApply[F] = new DerivedApply[F]

}
