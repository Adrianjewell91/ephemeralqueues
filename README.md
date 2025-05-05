Test complete queueing and draining in parallel:

for j in {0..99}; do
(
for i in {0..99}; do curl --location --request POST 'localhost:8080/queue/'$j'/add/'$i; done
for i in {0..99}; do curl --location --request GET 'localhost:8080/queue/'$j'/poll'; done
) &
done

---

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

https://unix.stackexchange.com/questions/704056/how-might-i-execute-this-nested-for-loop-in-parallel

---

wow I have learned a lot about systems just from exploring the queue system. Learnings:
1. Memory: 1000 queue * 1000 Capacity * 8 bytes (long) = 8 Mb, size , usage is quadratic.
2. Threads: some things are thread safe, some are not, sometimes you can’t tell where the multi threading is happening or not.
3. Threads: number of threads == num cores. They are related.
4. Server: there are multiple threads running and accessing the same system. Thankfully the queues were thread safe, actually I didn’t try that kind of load test.
5. Refactoring, lots of things to refactor got out of hand quickly.
6. Code: it is not dry lol. 