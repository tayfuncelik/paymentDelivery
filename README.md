# Challenge

## :computer: How to execute

_Provide a description of how to run/execute your program..._

After run docker-compose up necessary environment will be ready to use 
1-execute 'mvn clean install ' and then 'mvn spring-boot:run' inside delivery/producer folder
producer service will send online and offline events to Kafka.
2-execute 'mvn clean install ' and then 'mvn spring-boot:run' inside delivery/payment

I just tried to send few kafka message events using another module which is producer. 
But I just kept it simple you may able to change and use it.


## :memo: Notes

_Some notes or explaination of your solution..._
I designed this system offline and online seperately from entity layer to service layer.
I took event messages and converted to neccessary object using Object Mapper. If I got any error I just send it to 
log system provider.

Events will be handled accourding to their topics. Business logic will be processed. When any persistence problem will be send
to logging system as a Database error but if there is any mapping problem it will be Network error.


## :pushpin: Things to improve

_If u have more time or want to improve somthing..._

If more events will be handled Kafka Stream should be considered because of the big data process.
CI/CD pipeline integration might be added.
Logging monitoring could be implemented like KIBANA, Grafana ,LogStash ..
Consumers might commit after process completed but for now I just configured default as auto commit this way 
give us more latency but data consistency should be considered.
All methods might be covered with test cases
Repositories might be reached using Accound and payment service via interfaces and implp classes