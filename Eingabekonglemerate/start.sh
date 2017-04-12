mkdir $1

time java -jar rosinen.jar Kreis-8.txt > $1/Kreis-8.txt &
time java -jar rosinen.jar Quadrat-6.txt > $1/Quadrat-6.txt &
time java -jar rosinen.jar Quadrat-8.txt > $1/Quadrat-8.txt &
time java -jar rosinen.jar Quadrat-13.txt > $1/Quadrat-13.txt &
time java -jar rosinen.jar Zufall-7.txt > $1/Zufall-7.txt &
time java -jar rosinen.jar Zufall-7_ns.txt > $1/Zufall-7_ns.txt &
time java -jar rosinen.jar Zufall-40.txt > $1/Zufall-40.txt &
time java -jar rosinen.jar Zufall-100.txt > $1/Zufall-100.txt &
wait