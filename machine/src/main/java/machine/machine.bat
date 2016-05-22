@echo off
for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -fullversion 2^>^&1') do set "java_version=%%j%%k%%l%%m"
if %java_version% LSS 18000 (
    echo Java either not found your machine or an unsupported version of Java was found.
    echo The minimum version of Java supported is Java 8.
    echo Consider installing the latest version of Java from the Oracle website.
    echo ^(http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html^)
    pause
    exit
) else (
    start javaw -jar machine.jar
    exit
)