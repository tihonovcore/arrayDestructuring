# Destructuring assignment
### Задание к стажировке

### Array Destructuring

[Converter](https://github.com/tihonovcore/arrayDestructuring/blob/master/src/arrayDestructuring/Converter.java "Converter")
Основной класс<br>
**Вспомогательные классы:** <br>
[BlockDefinition](https://github.com/tihonovcore/arrayDestructuring/blob/master/src/arrayDestructuring/BlockDefinition.java "BlockDefinition")
Определяет блок как текстовое представление объявления массивов, текствое представление всего остального и *Map* указывающий по имени массива список переменных которые он инициализирует<br>
[ConvertFunctionVisitor](https://github.com/tihonovcore/closureConversion/blob/master/src/closureConversion/ConvertFunctionVisitor.java "CFV")
Преобразует объявление переменных, определенных значением элемента массива<br>

[Запуск](https://github.com/tihonovcore/arrayDestructuring/blob/master/Tester.sh) <code>./Tester.sh</code>, а затем 
<code>build</code>, <code>test</code>, или <code>run</code><br>
[Тесты](https://github.com/tihonovcore/arrayDestructuring/tree/master/src/arrayDestructuring/tests/input "Тесты")<br>
[ConverterTest](https://github.com/tihonovcore/arrayDestructuring/blob/master/src/arrayDestructuring/tests/ConverterTest.java "ConverterTest")
Проверяет наличие правильных определений переменных, иногда проверяет порядок строк