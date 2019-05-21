#!/bin/bash

compileOptions="--add-exports=jdk.scripting.nashorn/jdk.nashorn.internal.ir=ALL-UNNAMED --add-exports=jdk.scripting.nashorn/jdk.nashorn.internal.ir.visitor=ALL-UNNAMED --add-exports=jdk.scripting.nashorn/jdk.nashorn.internal.parser=ALL-UNNAMED --add-exports=jdk.scripting.nashorn/jdk.nashorn.internal.runtime=ALL-UNNAMED --add-exports=jdk.scripting.nashorn/jdk.nashorn.internal.runtime.options=ALL-UNNAMED"

sourcePath="$(pwd)/src/arrayDestructuring"
outPath="$(pwd)/testout"
libsPath="${sourcePath}/tests/junit-4.11.jar:${sourcePath}/tests/hamcrest-core-1.3.jar"
testsPath="${outPath}/arrayDestructuring/tests/input"
testClass="arrayDestructuring.tests.ConverterTest"
testClassPath="arrayDestructuring/tests/ConverterTest.class"

function build() {
    echo "building..."

    mkdir -p ${outPath}

    javac ${compileOptions} -d ${outPath} ${sourcePath}/*.java
    javac -cp "${libsPath}:${outPath}" ${compileOptions} -d ${outPath} ${sourcePath}/tests/*.java

    mkdir -p ${testsPath}
    cp ${sourcePath}/tests/input/*.js ${testsPath}
}

function tests() {
    echo "testing..."

    cd testout
    java -cp ${libsPath}:${testClassPath}: org.junit.runner.JUnitCore ${testClass}
    cd ..
}

function run() {
    echo "Enter path to file to convert: "
    read file

    java -cp ./testout arrayDestructuring.Converter ${file}
}


function help() {
    echo "commands:"
    echo "build - build project"
    echo "test - run tests"
    echo "run - run Converter, which offer enter path to file to convert"
    echo "help - help"
    echo "exit - exit"
}

help
while [[ true ]]
do
    echo "Enter command: "
    read input

    case ${input} in
    "build" )
        build
    ;;
    "test" )
        tests
    ;;
    "run" )
        run
    ;;
    "help" )
        help
    ;;
    "exit" )
        exit
    ;;
    esac
done
