# Play Framework + Elm example

サーバサイドを Play Framework、フロントエンドを Elm による SPA で構築する際のプロジェクトの例です。

## このプロジェクトの特徴

- Play Framework の一般的なプロジェクト構成に npm プロジェクトである `ui` フォルダを設けています
- `sbt run` を実行すると webpack の DevServer と elm-analyse も起動します
  - 実際は `ui` フォルダ内で `npm run dev` と `npm run analyse` を実行しています
- DevServer は全リクエストを Play Framework に（[http://localhost:9000] に）proxy します
- `sbt stage` や `sbt dist` を実行すると Webpack によるバンドルを行い Play Framework のアセットとして扱うように配置します
  - 実際は `ui` フォルダ内で `npm run build` を実行しています

## 使い始める前に

`ui` フォルダをカレントにして `npm ci` を実行してください。

## 画像などのアセットの扱いについて

画像などのアセットの配置は Play Framework 側に行います。実際は `public` フォルダ以下に配置します。Elm 側にはその在処の URL
を flags 経由で渡します。在り処の URL を実行時に渡す理由は以下のとおりです。

- Play Framework はアプリケーションモードによってアセットの URL が変わる（production モードだと md5 値をつけてくれたりする）
- Play Framework がコンテキストパス付きで起動されることがありえるので絶対パスでの指定が決め打ちでできない
- SPA が起動されるパスがルートとは限らないので決め打ちの相対パスでも指定しにくい

## CSS の扱いについて

CSS については以下のいずれかまたは以下の組み合わせを用いることができます。

- `ui` フォルダ内で CSS や SASS ファイルを作成し、`src/index.js` 内で `require` して Webpack でバンドルする。このプロジェクトで用いているやり方
- [elm-css](https://package.elm-lang.org/packages/rtfeldman/elm-css/latest/) を用いる。CSS 内で画像を用いるときは特におすすめ
- Play Framework 側に CSS や less ファイルを作成して処理する。読み込みは `views/index.scala.html` 内で行う。Hot Module Replacement（HMR）が効かないのであまりおすすめしない

## Play と DevServer、elm-analyse の同時起動はうざい

デスヨネー。

build.sbt の以下の行をコメントアウトしてください。

```scala
PlayKeys.playRunHooks += Npm.elmDev(baseDirectory.value)
```

## Webpack でバンドルされたフロントエンドモジュールはどこにいく？

`public/javascripts` フォルダに `main.js` として作成されます。
