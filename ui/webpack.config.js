const path = require('path')
const webpack = require('webpack')
const {merge} = require('webpack-merge')

const mode = process.env.NODE_ENV

/**
 * Webpack 用 Sass ローダーの設定
 */
const sassLoader = {
  loader: 'sass-loader',
  options: {
    implementation: require('sass'),
    sassOptions: {
      fiber: require('fibers')
    },
    sourceMap: true
  }
}

/**
 * 共通設定
 */
const common = {
  mode,
  entry: './src/index.js',
  output: {
    // ビルド成果物は Play Framework の管轄下へ配置
    path: path.resolve(__dirname, '../public/javascripts'),
    // アセットの公開パスは Play Framework に合わせる
    publicPath: '/assets/javascripts'
  },
  module: {
    rules: [
      // あまり意味がないと思うけど JavaScript は Babel で処理するようにローダーを設定する
      {
        test: /\.m?js$/,
        exclude: /node_modules/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: [['@babel/preset-env', {targets: 'defaults'}]]
          }
        }
      },
      // CSS モジュールのローダーの設定
      {
        test: /\.module\.s?css$/i,
        use: [
          'style-loader',
          {
            loader: 'css-loader',
            options: {
              modules: {
                localIdentName: '[name]__[local]'
              }
            }
          },
          sassLoader
        ]
      },
      // グローバルな CSS のローダーの設定
      {
        test: /^((?!\.module).)+\.s?css$/i,
        use: ['style-loader', 'css-loader', sassLoader]
      }
    ]
  }
}

if (mode === 'development') {
  /* 開発モード時 */
  console.log('building for development...')
  module.exports = merge(common, {
    plugins: [
      // ビルドエラーが発生しても Webpack が終了しないようにするプラグイン
      new webpack.NoEmitOnErrorsPlugin()
    ],
    module: {
      rules: [
        // Elm 用ローダ
        {
          test: /\.elm$/,
          exclude: [/elm-stuff/, /node_modules/],
          use: [
            {loader: 'elm-hot-webpack-loader'},
            {
              loader: 'elm-webpack-loader',
              options: {
                // 開発モードなのでデバッグ機能を有効にする
                debug: true
              }
            }
          ]
        }
      ]
    },
    devtool: 'source-map',
    // dev-server （開発サーバ）の設定
    devServer: {
      hot: true,
      // サーバサイドは Play Framework が受け持つので全リクエストを proxy で Play Framework に流す
      proxy: {
        '/': {
          target: 'http://localhost:9000/'
        }
      }
    }
  })
} else if (mode === 'production') {
  /* 製品モード時 */
  console.log('Building for production...')
  module.exports = merge(common, {
    module: {
      rules: [
        // Elm 用ローダ
        {
          test: /\.elm$/,
          exclude: [/elm-stuff/, /node_modules/],
          use: [
            {
              loader: 'elm-webpack-loader',
              options: {
                // 製品モードなので最適化を有効にする
                optimize: true
              }
            }
          ]
        }
      ]
    }
  })
}
