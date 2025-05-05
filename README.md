ngrok https --url=sunfish-sacred-dogfish.ngrok-free.app 8080


curl --location --request POST 'https://5ca6-73-219-87-79.ngrok-free.app/queue';

curl --location --request POST 'https://5ca6-73-219-87-79.ngrok-free.app/queue/0/addition/3';

curl --location --request POST 'https://5ca6-73-219-87-79.ngrok-free.app/queue/0/addition/4';

curl --location --request POST 'https://5ca6-73-219-87-79.ngrok-free.app/queue/0/addition/5';

curl --location 'https://5ca6-73-219-87-79.ngrok-free.app/polling/0';

curl --location 'https://5ca6-73-219-87-79.ngrok-free.app/polling/0';

curl --location 'https://5ca6-73-219-87-79.ngrok-free.app/polling/0';

curl --location --request DELETE 'https://5ca6-73-219-87-79.ngrok-free.app/queue/0';



https://unix.stackexchange.com/questions/103920/parallelize-a-bash-for-loop 

N=10
(
for i in {1..100}; do ((i=i%N)); ((i++==0)) && wait; curl --location --request POST 'localhost:8080/queue' &; done
)


for j in {0..99}; do

for i in {0..99}; do curl --location --request POST 'localhost:8080/queue/'$j'/add/'$i; done &

done 


for j in {0..99}; do

for i in {0..99}; do curl --location --request GET 'localhost:8080/queue/'$j'/poll'; done &

done

https://unix.stackexchange.com/questions/704056/how-might-i-execute-this-nested-for-loop-in-parallel

for j in {0..99}; do
(
for i in {0..99}; do curl --location --request POST 'localhost:8080/queue/'$j'/add/'$i; done
for i in {0..99}; do curl --location --request GET 'localhost:8080/queue/'$j'/poll'; done
) &
done


100 concurrent seems ok locally. some requests get dropped, why is that and how to detect it? 

-- 
for i in {1..5000}; do curl --location --request POST 'localhost:8080/queue/1/add/'$i; done

for i in {1..5000}; do curl --location --request GET 'localhost:8080/queue/'$i'/poll'; done