# [kafka搭建说明](../README.md)

- [kafka搭建说明](#kafka搭建说明)
    - [搭建环境](#搭建环境)
    - [单机环境搭建](#单机环境搭建)
    - [集群环境搭建](#集群环境搭建)

# 搭建环境

| 文档版本|   版本   |  更新说明  |更新时间 | 更新人 |
| ---------|-------|-------|-------|------------ |
| v1.0.0|   **3.8.5**  | 编写Kafka环境搭建文档 | 2021/1/2 | 张子尧 |

# 单机环境搭建

## 环境准备

### 安装JDK

由于Kafka是用Scala实现的，所以需要jdk的环境支持

```shell
yum install -y java-1.8.0-openjdk-devel.x86_64
```

### 安装zookeeper

Kafka是强依赖于zookeeper的，所以我们需要先安装一个zookeeper

```shell
#下载
wget https://mirror.bit.edu.cn/apache/zookeeper/zookeeper-3.5.8/apache-zookeeper-3.5.8-bin.tar.gz
#解压
tar -zxvf apache-zookeeper-3.5.8-bin.tar.gz
#配置conf目录下的配置文件
cp -p conf/zoo_sample.cfg conf/zoo.cfg
#启动zookeeper
sh bin/zkServer.sh start 
#连接zookeeper客户端
sh bin/zkCli.sh
```

## 安装Kafka

### 下载kafka

下载2.4.1release版本，解压

```shell
wget https://mirror.bit.edu.cn/apache/kafka/2.4.1/kafka_2.11-2.4.1.tgz  # 2.11是scala的版本，2.4.1是kafka的版本
tar -xzf kafka_2.11-2.4.1.tgz
cd kafka_2.11-2.4.1
```

### 修改配置文件

修改server.properties

```sh
vim config/server.properties

#修改brokerid要确保在整个集群中brokerid唯一
broker.id=0
#设置Kafka机器IP和提供服务的端口
listeners=PLAINTEXT://127.0.0.1:9092
#修改kafka消息存储文件
log.dir=~/data/kafka-logs
#设置zookeeper连接地址
zookeeper.connect=127.0.0.1:2181
```

### 启动服务

```sh
#后台启动kafka 
sh bin/kafka-server-start.sh -daemon conf/server.properties
#查看启动日志
tail -500f logs/kafkaServer.out
#进入zk查看Kafka信息
bin/zkCli.sh 

ls /brokers/ids
#停止Kafka
bin/kafka-server-stop.sh
```

### 常用命令

```sh
#创建主题
bin/kafka-topics.sh --create --zookeeper 127.0.0.1:2181 --replication-factor 1 --partitions 1 --topic kafka-topic
#删除主题
bin/kafka-topics.sh --delete --topic test --zookeeper 127.0.0.1:2181
#查看broker存在的主题
bin/kafka-topics.sh --list --zookeeper 127.0.0.1:2181
#查看topic详情
 bin/kafka-topics.sh --describe --zookeeper 127.0.0.1:2181 --topic kafka-topic
#发送消息
bin/kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic kafka-topic 
>this is first message to zhang!!!
>
#消费消息(注意：默认只消费最新的消息，如果想要消费之前的消息，通过指参数。--from-beginning)
bin/kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic kafka-topic
#消费多个主题
bin/kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --whitelist "kafka-topic01|kafka-topic02"
#单播（消费者在同一个消费组中则只会有一个消费之消费到消息）
#多播（消费这在不同的消费组中，则每个消费组只会要一个消费者消费到消息）

#查看消费组名
bin/kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --list
#查看消费组偏移量
bin/kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --describe --group testGroup
```

| 参数名         | 说明                                   |
| -------------- | -------------------------------------- |
| current-offset | 当前消费组已经消费到的偏移量位置       |
| Log-end-offset | 主题对应的parttion消息结束的偏移量位置 |
| lag            | 当前消费组未消费的消息数               |



# 集群环境搭建

## 环境准备

### 机器准备

```sh
#准备三台机器
192.168.2.1 broker1
192.168.2.2 broker1
192.168.2.3 broker3
```

### 环境安装

分别在三台机器上安装，jdk，和下载好kafka

### 修改配置文件

分别修改三台机器上的server.properties

```sh
vim config/server.properties

#修改brokerid要确保在整个集群中brokerid唯一
broker.id=0 #其他两台机器修改brokerid时要确保id唯一
#设置Kafka机器IP和提供服务的端口
listeners=PLAINTEXT:192.168.2.1:9092  
#修改kafka消息存储文件
log.dir=~/data/kafka-logs
#设置zookeeper连接地址
zookeeper.connect=127.0.0.1:2181
```

### 启动集群

分别启动三台broker

```sh
#broker1 
bin/kafka-server-start.sh -daemon config/server.properties
#broker2 
bin/kafka-server-start.sh -daemon config/server.properties
#broker3 
bin/kafka-server-start.sh -daemon config/server.properties
```

登陆zookeeper客户端查看broker是否注册成功

```sh
#连接zookeeper客户端
sh bin/zkCli.sh
#查看节点数
ls /brokers/ids
```

