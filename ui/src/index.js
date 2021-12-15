require('normalize.css')
require('./global.scss')

const { Elm } = require('./Main.elm')

Elm.Main.init({ flags: global.flags })
