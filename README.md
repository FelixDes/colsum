<div style="right:0; position: absolute; width: 100px; height: 150px; opacity: 80%">
    <div style="position: absolute; width: 50px; height: 50px; background-color: blue; z-index: 2"></div>
    <div style="top: 30px; left: 30px; position: absolute; width: 50px; height: 50px; background-color: aqua; z-index: 1"></div>
    <div style="top: 30px; left: 30px; position: absolute; width: 50px; height: 50px; background-color: rgba(0,255,255,0.54); z-index: 3"></div>
</div>

# $ colsum

Simple command line tool for overlaying colors written in Kotlin

```shell
java -jar colsum.jar -b "lightYellow" -e "rgb(55, 12, 2, 0.4) + rgba(1, 50, 217, 0.3)"
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
  "rgb(" (percent-or-none, ){2} percent-or-none [, alpha-value ] ")"  /
  "rgb(" (number-or-none, ){2} number-or-none [, alpha-value ] ")"    /
  "rgb(" percent-or-none{3} [ / alpha-or-none ] ")"                   /
  "rgb(" number-or-none{3} [ / alpha-or-none ] ")"

rgba-function = 
  "rgba(" (percent-or-none, ){2} percent-or-none [, alpha-value ] ")"  /
  "rgba(" (number-or-none, ){2} number-or-none [, alpha-value ] ")"    /
  "rgba(" percent-or-none{3} [ / alpha-or-none ] ")"                   /
  "rgba(" number-or-none{3} [ / alpha-or-none ] ")"

percent-or-none = percent / none

number-or-none = number / none

alpha-or-none = alpha-value / none

hex-color = 
  "#" HEXDIG{3} [ HEXDIG ]        /
  "#" hex-group{3} [ hex-group ]  /

hex-group = HEXDIG HEXDIG

calc-function = "calc(" calc-sum ")"  

calc-sum = calc-product [ [ " + " / " - " ] calc-product ]*  

calc-product = calc-value [ [ " * " / " / " ] calc-value ]*  

calc-value = 
  number           /
  percent          /
  calc-constant    /
  calc-function    /
  '(' calc-sum ')'   

calc-constant = 
  e          /
  pi         /
  infinity   /
  -infinity  /
  NaN
  
number = 1; regex: ^[+\-]?(?:0|[1-9]\d*)(?:\.\d+)?(?:[eE][+\-]?\d+)?
percent = 1%; regex: ^[+\-]?(?:0|[1-9]\d*)(?:\.\d+)?(?:[eE][+\-]?\d+)?%
```

# 🧑‍💻 For new contributors

🚧 Under construction 🚧

# 🔧 Internals

A brief structural components overview

## 🔍️ Parser

## 🎨 Alpha composition formulas

Сolors are superimposed like, for example, in the Mozilla Firefox browser. Formulas are presented below:

```
result alpha = background alpha * (1 - new alpha) + new alpha
result color[R,G,B] = background alpha * (1 - new alpha) * background color + new color * new alpha
```