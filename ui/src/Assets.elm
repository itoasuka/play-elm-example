module Assets exposing (Assets, assetsDecoder, emptyAssets)

import Json.Decode as D


type alias Images =
    { elmLogo : String
    }


emptyImages : Images
emptyImages =
    Images ""


type alias Assets =
    { images : Images
    }


emptyAssets : Assets
emptyAssets =
    Assets emptyImages


imagesDecoder : D.Decoder Images
imagesDecoder =
    D.map Images
        (D.field "elmLogo" D.string)


assetsDecoder : D.Decoder Assets
assetsDecoder =
    D.map Assets
        (D.field "images" imagesDecoder)
        |> D.field "assets"
