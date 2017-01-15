package io.soheila.cms.utils.validators

import scalaz._
import scalaz.Scalaz._

trait Validator[E] {
  def checkNonEmpty(error: E)(s: String): ValidationNel[E, String] =
    if (s != null || !s.isEmpty) s.successNel else error.failureNel
  def checkNonNull(error: E)(s: Any): ValidationNel[E, Any] =
    if (Option(s).isEmpty) error.failureNel else s.successNel
}
