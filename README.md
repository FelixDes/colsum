<div style="right:0; position: absolute; width: 100px; height: 150px; opacity: 80%">
    <div style="position: absolute; width: 50px; height: 50px; background-color: blue; z-index: 2"></div>
    <div style="top: 30px; left: 30px; position: absolute; width: 50px; height: 50px; background-color: aqua; z-index: 1"></div>
    <div style="top: 30px; left: 30px; position: absolute; width: 50px; height: 50px; background-color: rgba(0,255,255,0.54); z-index: 3"></div>
</div>

# $ colsum

Simple command line tool for overlaying colors written in Kotlin

```shell
java -jar colsum.jar -b "aqua" -e "rgb(55, 12, 2, 0.4) + rgba(1, 50, 217, 0.3)"
```

<div style="font-size: 20px">
Usage:<br>
    <div style="margin-left: 25px">
    — expression, -e -> Expression for computation (always required) { String }<br>
    — background, -b [#FFF] -> background color { String }<br>
    — help, -h -> Usage info<br>
    </div>
</div>

# 🧑‍💻 For new contributors

🚧 Under construction 🚧

# 🔧 Internals

A brief structural components overview

## 🔍️ Parser

🚧 Under construction 🚧

## 🎨 Alpha composition formulas

Сolors are superimposed like, for example, in the Mozilla Firefox browser. Formulas are presented below:

```
result alpha = background alpha * (1 - new alpha) + new alpha
result color[R,G,B] = background alpha * (1 - new alpha) * background color + new color * new alpha
```