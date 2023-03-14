rm -r bin
rm -r filelist.txt
mkdir bin

find ./src -name *.java -print > filelist.txt
javac -d ./bin -cp ./lib/commons-dbcp-1.4.jar:./lib/jsch-0.1.55.jar:./lib/ojdbc14_g10g102.jar @filelist.txt
