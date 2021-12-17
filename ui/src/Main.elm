module Main exposing (Model, Msg, main)

import Assets exposing (Assets, assetsDecoder, emptyAssets)
import Browser exposing (Document, UrlRequest)
import Browser.Navigation as Nav
import Html exposing (button, div, img, text)
import Html.Attributes exposing (class, height, src, type_, width)
import Html.Events exposing (onClick)
import Http
import Json.Decode as D
import Url exposing (Url)



-- MODEL


type alias Model =
    { key : Nav.Key
    , maybeGreeting : Maybe String
    , maybeError : Maybe String
    , assets : Assets
    }


init : D.Value -> Url -> Nav.Key -> ( Model, Cmd Msg )
init flags _ key =
    let
        {- flags のデコードに失敗したら空の Assets を設定していますが、失敗した旨表示できるようにすると
           よりはかどります
        -}
        assets : Assets
        assets =
            flags
                |> D.decodeValue assetsDecoder
                |> Result.withDefault emptyAssets
    in
    ( Model key Nothing Nothing assets, Cmd.none )



-- UPDATE


type Msg
    = ChangedUrl Url
    | ClickedLink UrlRequest
    | RequestGreeting
    | GotGreetingResult (Result Http.Error String)


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        ChangedUrl _ ->
            {- URL が変更されたときの挙動。適宜定義してください -}
            ( model, Cmd.none )

        ClickedLink _ ->
            {- リンクがクリックされたときの挙動。適宜定義してください -}
            ( model, Cmd.none )

        RequestGreeting ->
            {- 挨拶をサーバにリクエストする -}
            let
                cmd : Cmd Msg
                cmd =
                    Http.request
                        { method = "GET"
                        , headers =
                            [ Http.header "X-Requested-With" "XMLHttpRequest" ]
                        , url = "/greeting"
                        , body = Http.emptyBody
                        , expect =
                            Http.expectJson GotGreetingResult D.string
                        , timeout = Nothing
                        , tracker = Nothing
                        }
            in
            ( model, cmd )

        GotGreetingResult result ->
            case result of
                Ok value ->
                    ( { model | maybeGreeting = Just value }, Cmd.none )

                Err error ->
                    let
                        errorMessage : String
                        errorMessage =
                            case error of
                                Http.BadUrl string ->
                                    "URL がおかしい :" ++ string

                                Http.Timeout ->
                                    "タイムアウトした"

                                Http.NetworkError ->
                                    "ネットワークがおかしい"

                                Http.BadStatus int ->
                                    String.fromInt int ++ " なんてコードが帰ってきた"

                                Http.BadBody string ->
                                    "レスポンスボディがおかしい : " ++ string
                    in
                    ( { model | maybeError = Just errorMessage }, Cmd.none )



-- VIEW


view : Model -> Document Msg
view model =
    { title = "Play + Elm example"
    , body =
        [ div [ class "greeting" ]
            [ div []
                [ img [ src model.assets.images.elmLogo, width 240, height 240 ] [] ]
            , div []
                [ button [ type_ "button", onClick RequestGreeting ] [ text "ごあいさつ" ] ]
            , div []
                [ model.maybeGreeting |> Maybe.withDefault "" |> text ]
            , div []
                [ model.maybeError |> Maybe.withDefault "" |> text ]
            ]
        ]
    }



-- SUBSCRIPTIONS


subscriptions : Model -> Sub Msg
subscriptions _ =
    Sub.none



-- MAIN


main : Program D.Value Model Msg
main =
    Browser.application
        { init = init
        , view = view
        , update = update
        , subscriptions = subscriptions
        , onUrlChange = ChangedUrl
        , onUrlRequest = ClickedLink
        }
