package controllers

import javax.inject._
import play.api._
import play.api.libs.json.{JsString, Json}
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the application's home page.
 */
@Singleton
class HomeController @Inject() (val controllerComponents: ControllerComponents, application: Application)
    extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method will be called when the application receives a `GET`
   * request with a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    // Elm に flags で渡すものを定義する
    val flagsJson = Json.obj(
      // アセットの URL
      "assets" -> Json.obj(
        // 画像
        "images" -> Json.obj(
          // Elm ロゴ
          "elmLogo" -> routes.Assets.versioned("images/Elm_logo.svg.png").url
        )
      )
    )

    Ok(views.html.index(application.mode, flagsJson.toString()))
  }

  def greeting() = Action {
    Ok(JsString("Hello!"))
  }
}
