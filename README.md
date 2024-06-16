## Build
to build you need JDK > 14 and maven 3.9.6 installed.

```bash
git clone
cd 
mvn clean install
# should have a jar file
java -jar ...
```


## To do a test run after compiling with javac

```bash
cat test_success.ts | java -classpath .\target\classes com.garliclord.spalk.Main
```