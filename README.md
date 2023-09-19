<div style="right:0; position: absolute; width: 100px; height: 150px; opacity: 80%">
    <div style="position: absolute; width: 50px; height: 50px; background-color: blue; z-index: 2"></div>
    <div style="top: 30px; left: 30px; position: absolute; width: 50px; height: 50px; background-color: aqua; z-index: 1"></div>
    <div style="top: 30px; left: 30px; position: absolute; width: 50px; height: 50px; background-color: rgba(0,255,255,0.54); z-index: 3"></div>
</div>

# $ colsum

Simple command line tool for overlaying colors implemented in Kotlin

```shell
java -jar colsum.jar -b "lightyellow" -e "rgb(55, 12, 2, 0.4) + rgba(1, 50, 217, 0.3)"
```

<div style="font-size: 20px">
Usage:<br>
    <div style="margin-left: 25px">
    — expression, -e -> Expression for computation (always required) { String }<br>
    — background, -b [#FFF] -> background color { String }<br>
    — help, -h -> Usage info<br>
    </div>
</div>

# Grammar

Grammar(abnf) of expressions:

```abnf
root = color [ " + " color ]*

color = color-function  /  hex-color  /  named-color

named-color = white ; https://developer.mozilla.org/en-US/docs/Web/CSS/named-color

color-function = rgb-function  /  rgba-function  /  hsl-function  /  hsla-function

rgb-function = 
  "rgb(" ( rgb-arg ", " ){2} rgb-arg [ ", " alpha-arg ] ")"  /
  "rgb(" ( rgb-arg ", " ){2} rgb-arg [ ", " rgb-arg ] ")"

rgba-function = 
  "rgba(" ( rgb-arg ", " ){2} rgb-arg [ ", " alpha-arg ] ")"  /
  "rgba(" ( rgb-arg ", " ){2} rgb-arg [ ", " rgb-arg ] ")"

hsl-function = 
  "hsl(" hue ( ", " percent-or-none ){2} [ ", " alpha-arg ] )  /
  "hsl(" hue percent-or-none{2} [ " / " alpha-arg ] ")"

hsla-function = 
  "hsla(" hue ( ", " percent-or-none ){2} [ ", " alpha-arg ] )  /
  "hsla(" hue percent-or-none{2} [ " / " alpha-arg ] ")"

hue = number  /  angle  /  none

rgb-arg = number  /  percent-or-none

percent-or-none = percent  /  none

alpha-arg = percent  /  number

hex-color = 
  "#" hex-group{3} [ hex-group ]  /
  "#" HEXDIG{3} [ HEXDIG ]

hex-group = HEXDIG HEXDIG

calc-function = "calc(" calc-sum ")"  

calc-sum = calc-product [ [ " + "  /  " - " ] calc-product ]*  

calc-product = calc-value [ [ " * "  /  " / " ] calc-value ]*  

calc-value = 
  number            /
  percent           /
  calc-constant     /
  calc-function     /
  '(' calc-sum ')'   

calc-constant = 
  e          /
  pi         /
  infinity   /
  -infinity  /
  NaN
  
number = 1; regex: ^[+\-]?(?:0|[1-9]\d*)(?:\.\d+)?(?:[eE][+\-]?\d+)?
percent = number "%"
angle = number ("deg"  /  "grad"  /  "turn"  /  "rad")
```

# 🧑‍💻 For new contributors

🚧 Under construction 🚧

# 🔧 Internals

A brief structural components overview

## 🔍️ Parser

## 🎨 Alpha composition formulas

Сolors are superimposed like, for example, in the Mozilla Firefox browser. Formulas are presented below:

```
resAlpha = bgAlpha + addingAlpha * (1 - bgAlpha)
resRed = (bgRed * bgAlpha * (1 - addingAlpha) + addingRed * addingAlpha) / resAlpha
```