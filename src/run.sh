if [ ! -d "../bin" ]; then
  mkdir ../bin
fi
javac tomasulo/*.java -d ../bin/
cd ../bin
java tomasulo/Main
