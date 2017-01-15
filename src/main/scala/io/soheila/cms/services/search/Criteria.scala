package io.soheila.cms.services.search

import play.api.libs.json._

/**
 * Parser Combinators eventually build a criteria from the user input.
 * Criteria.scala was taken and modified from [[https://github.com/arturopala/play-2.4-crud-with-reactive-mongo]]
 *
 * Criteria is a top-level expression used to query/filter documents in MongoDB.
 * Can be used to parse and evaluate existing JSON queries
 * or to construct them type-safe way.
 */
trait Criteria {
  def toJson: JsObject
  def &&(other: Criteria): Criteria = Criteria.And(this, other)
  def ||(other: Criteria): Criteria = Criteria.Or(this, other)
}

/**
 * Constraint is an expression's operator used to constraint .
 */
trait Constraint {
  def toJson: JsObject
}

/**
 * Supported criteria and constraints set.
 */
object Criteria {

  import CriteriaUtils._

  /**Implicit equality criteria*/
  case class Eq(path: String, pattern: JsValue) extends Criteria with PathBased {
    override def toJson = Json.obj(path -> pattern)
  }

  object Eq {
    def apply(path: String, value: String): Eq = new Eq(path, JsString(value))
    def apply(path: String, value: Int): Eq = new Eq(path, JsNumber(value))
    def apply(path: String, value: Double): Eq = new Eq(path, JsNumber(value))
    def apply(path: String, value: Boolean): Eq = new Eq(path, JsBoolean(value))
  }

  case class In(path: String, pattern: JsValue) extends Criteria with PathBased {
    override def toJson = Json.obj(path -> Json.obj("$in" -> pattern))
  }

  object In {
    def apply(path: String, values: Seq[String]): In = new In(path, JsArray(values.map(a => JsString(a))))
  }

  case class Near(path: String, lon: Double, lat: Double, minDistance: Double = 1, maxDistance: Double = 10000) extends Criteria with PathBased {
    override def toJson = Json.obj(
      path -> Json.toJson(Json.obj(
        "$near" -> Json.toJson(Json.obj(
          "$geometry" -> Json.toJson(Json.obj(
            "type" -> "Point",
            "coordinates" -> Json.arr(lon, lat)
          )),
          "$maxDistance" -> Json.toJson(maxDistance),
          "$minDistance" -> Json.toJson(minDistance)
        ))
      ))
    )
  }

  case class If(path: String, c: Constraint) extends Criteria with PathBased {
    override def toJson = Json.obj(path -> c.toJson)
  }

  object If {
    def apply(t: (String, Constraint)): If = new If(t._1, t._2)
  }

  //Logical Query Constraints //

  /**Joins criteria with a logical AND, returns all that match the conditions of both clauses*/
  case class And(cs: Criteria*) extends Criteria {
    override def toJson = Json.obj(AndConstraint.key -> JsArray(cs map (_.toJson)))
  }

  /**Joins criteria with a logical OR, returns all that match the conditions of both clauses*/
  case class Or(cs: Criteria*) extends Criteria {
    override def toJson = Json.obj(OrConstraint.key -> JsArray(cs map (_.toJson)))
  }

  /**Inverts the effect of a nested operator(s)*/
  case class Not(c: Constraint) extends Constraint {
    override def toJson = Json.obj(NotConstraint.key -> c.toJson)
  }

  // Comparison Query Constraints //

  /**Matches values that are not equal to a specified value*/
  case class Ne(pattern: JsValue) extends SimpleConstraint(NeConstraint.key)

  object Ne {
    def apply(value: String): Ne = new Ne(JsString(value))
    def apply(value: Int): Ne = new Ne(JsNumber(value))
    def apply(value: Double): Ne = new Ne(JsNumber(value))
    def apply(value: Boolean): Ne = new Ne(JsBoolean(value))
  }

  /**Matches values that are less than a specified value*/
  case class Lt(pattern: JsValue) extends SimpleConstraint(LtConstraint.key)

  object Lt {
    def apply(value: Int): Lt = new Lt(JsNumber(value))
    def apply(value: Double): Lt = new Lt(JsNumber(value))
  }

  case class Gt(pattern: JsValue) extends SimpleConstraint(GtConstraint.key)

  object Gt {
    def apply(value: Int): Gt = new Gt(JsNumber(value))
    def apply(value: Double): Gt = new Gt(JsNumber(value))
  }

  case class Lte(pattern: JsValue) extends SimpleConstraint(LteConstraint.key)

  object Lte {
    def apply(value: Int): Lte = new Lte(JsNumber(value))
    def apply(value: Double): Lte = new Lte(JsNumber(value))
  }

  case class Gte(pattern: JsValue) extends SimpleConstraint(GteConstraint.key)

  object Gte {
    def apply(value: Int): Gte = new Gte(JsNumber(value))
    def apply(value: Double): Gte = new Gte(JsNumber(value))
  }
}

object CriteriaUtils {

  trait PathBased {
    val path: String
    lazy val jsPath: JsPath = path.split('.').foldLeft(JsPath())((a, p) => a \ p)
  }

  trait ConstraintExtractor[A] {
    val key: String
    def extract(value: JsValue, obj: JsObject): A

    final def unapply(obj: JsObject): Option[A] =
      obj.value.get(key).map(value => extract(value, obj))
  }

  abstract class SimpleConstraintExtractor(val key: String) extends ConstraintExtractor[JsValue] {
    override def extract(value: JsValue, obj: JsObject): JsValue = value
  }

  /**Single value constraint*/
  abstract class SimpleConstraint(key: String) extends Constraint {
    val pattern: JsValue
    override def toJson = Json.obj(key -> pattern)
  }

  object IsConstraint {
    final def unapply(value: JsValue): Option[JsObject] =
      value match {
        case obj: JsObject =>
          if (obj.keys.exists(k => k.startsWith("$"))) Some(obj) else None
        case _ => None
      }
  }

  object JsArrayOfObjects {
    val isJsObject: JsValue => Boolean = {
      case JsObject(_) => true
      case _ => false
    }
    def unapply(jsValue: JsValue): Option[Seq[JsObject]] = jsValue match {
      case JsArray(seq) if seq forall isJsObject => Some(seq map (_.as[JsObject]))
      case _ => None
    }
  }

  object AndConstraint extends ConstraintExtractor[Seq[JsObject]] {
    val key = "$and"
    override def extract(value: JsValue, obj: JsObject) =
      value match {
        case JsArrayOfObjects(seq) => seq
        case _ => throw new IllegalArgumentException(s"$key query operator must be an array-of-objects")
      }
  }

  object OrConstraint extends ConstraintExtractor[Seq[JsObject]] {
    val key = "$or"
    override def extract(value: JsValue, obj: JsObject) =
      value match {
        case JsArrayOfObjects(seq) => seq
        case _ => throw new IllegalArgumentException(s"$key query operator must be an array-of-objects")
      }
  }

  object NotConstraint extends ConstraintExtractor[JsObject] {
    val key = "$not"
    override def extract(value: JsValue, obj: JsObject): JsObject = {
      value.as[JsObject]
    }
  }

  object EqConstraint extends SimpleConstraintExtractor("$eq")

  object NeConstraint extends SimpleConstraintExtractor("$ne")

  object LtConstraint extends SimpleConstraintExtractor("$lt")

  object GtConstraint extends SimpleConstraintExtractor("$gt")

  object LteConstraint extends SimpleConstraintExtractor("$lte")

  object GteConstraint extends SimpleConstraintExtractor("$gte")

}
