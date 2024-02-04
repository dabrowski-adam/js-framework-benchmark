package app

import outwatch.*
import outwatch.dsl.*
import cats.effect.SyncIO

import colibri.Subject

object Main:

  import Domain.*
  import Data.*
  import colibri.Observable

  //val lastId:    Subject[Int]          = Subject.behavior(0)
  val items:     Subject[Vector[Item]] = Subject.behavior(Vector.empty)
  val selection: Subject[Option[Int]]  = Subject.behavior(None)

  //private def addItems(n: Int): Observable[(Vector[Item], Int)] =
  //  foo.map((items, lastId) =>
  //    (items ++ Vector.tabulate(n)(i => Item(lastId + i, generateLabel())), lastId + n)
  //  )

  def main(args: Array[String]): Unit =
    Outwatch.renderInto[SyncIO]("#main", App).unsafeRunSync()

  private def App =
    div(cls := "container",
      div(cls := "jumbotron",
        div(cls := "row",
          div(cls := "col-md-6",
            h1("Outwatch"),
          ),
          div(cls := "col-md-6",
            div(cls := "row",
              //div(cls := "col-sm-6 smallpad",
              //  button(tpe := "button", cls := "btn btn-primary btn-block", idAttr := "run",
              //    onClick(addItems(1000)) --> foo,
              //    "Create 1,000 rows",
              //  )
              //),
              Button(id = "run",      text = "Create 1,000 rows",     action = _ => Vector.tabulate(1000)(_ => generateItem())),
              Button(id = "runlots",  text = "Create 10,000 rows",    action = _ => Vector.tabulate(10000)(_ => generateItem())),
              Button(id = "add",      text = "Append 1,000 rows",     action = _ ++ Vector.tabulate(1000)(_ => generateItem())),
              Button(id = "update",   text = "Update every 10th row", action = _.zipWithIndex.map(
                (item, index) => if (index % 10 == 0) item.copy(label = item.label + " !!!") else item
              )),
              Button(id = "clear",    text = "Clear",                 action = _ => Vector.empty),
              Button(id = "swaprows", text = "Swap Rows",             action = {
                case items if items.length > 998 => items.updated(1, items(998)).updated(998, items(1))
                case items                       => items
              }),
            ),
          ),
        ),
      ),
      table(cls := "table table-hover table-striped test-data",
        tbody(
          idAttr := "tbody",
          items.map(_.map(Row)), // TODO: Do this properly?
        ),
      ),
    )

  private def Button(id: String, text: String, action: Vector[Item] => Vector[Item]) =
    div(cls := "col-sm-6 smallpad",
      button(tpe := "button", cls := "btn btn-primary btn-block", idAttr := id,
        onClick(items.map(action)) --> items,
        text,
      )
    )

  private def Row(item: Item) =
    tr(cls <-- selection.map(_.contains(item.id)).toggle("danger"),
      td(cls := "col-md-1", idAttr := item.id.toString,
        item.id,
      ),
      td(cls := "col-md-4",
        a(cls := "lbl",
          onClick.as(Some(item.id)) --> selection,
          item.label,
        ),
      ),
      td(cls := "col-md-1",
        a(cls := "remove",
          onClick(items.map(_.filterNot(_.id == item.id))) --> items,
          span(cls := "remove glyphicon glyphicon-remove", dataAttr("aria-hidden") := "true"),
        ),
      ),
      td(cls := "col-md-6"),
    )

end Main

object Domain:

  case class Item(id: Int, label: String)

end Domain

object Data:

  import Domain.*
  import scala.collection.immutable.ArraySeq

  // TODO: Move lastId into Subject?
  def generateItem(): Item = Item(nextId(), Data.generateLabel())

  private var lastId: Int = 0
  private def nextId(): Int = { lastId += 1; lastId }

  def generateLabel(): String =
    import scala.util.Random

    val adjective = adjectives(Random.nextInt(adjectives.length))
    val colour    = colours(Random.nextInt(colours.length))
    val noun      = nouns(Random.nextInt(nouns.length))

    s"$adjective $colour $noun"

  private val adjectives = ArraySeq(
    "pretty",
    "large",
    "big",
    "small",
    "tall",
    "short",
    "long",
    "handsome",
    "plain",
    "quaint",
    "clean",
    "elegant",
    "easy",
    "angry",
    "crazy",
    "helpful",
    "mushy",
    "odd",
    "unsightly",
    "adorable",
    "important",
    "inexpensive",
    "cheap",
    "expensive",
    "fancy",
  )

  private val colours = ArraySeq(
    "red",
    "yellow",
    "blue",
    "green",
    "pink",
    "brown",
    "purple",
    "brown",
    "white",
    "black",
    "orange",
  )

  private val nouns = ArraySeq(
    "table",
    "chair",
    "house",
    "bbq",
    "desk",
    "car",
    "pony",
    "cookie",
    "sandwich",
    "burger",
    "pizza",
    "mouse",
    "keyboard",
  )

end Data