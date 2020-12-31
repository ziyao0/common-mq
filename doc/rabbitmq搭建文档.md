# [rabbitmq搭建说明](../README.md)

- [rabbitmq搭建说明](#rabbitmq搭建说明)
    - [搭建环境](#搭建环境)
    - [单机环境搭建](#单机环境搭建)
        - [上传安装包到服务器上](##上传安装包到服务器上)
        - [安装Erlang语言](##安装Erlang语言)
        - [安装rabbitmq](##安装rabbitmq)
        - [开启界面管理及配置](##开启界面管理及配置)
        - [常用命令](##常用命令)
        - [配置文件路径](##配置文件路径)
        - [登录测试](##登录测试)
        - [常见问题](##常见问题)

    - [集群环境搭建](#集群环境搭建)
        - [环境准备](##环境准备)
        - [集群搭建](##集群搭建)
        - [负载均衡-HAProxy](##负载均衡-HAProxy)
            - [简介](###简介)
            - [HAProxy搭建](###HAProxy搭建)
            - [HAProxy配置详解](###HAProxy配置详解)

# 搭建环境

| 文档版本|   版本    |   服务器版本    |  更新说明  |更新时间 | 更新人 |备注|
| ---------|-------|-------|-------|--------|------------ |------|
| v1.0.0|   **3.8.5**  |  CentOS7.6  |rabbitmq单机环境搭建文档 | 2020/12/8 | 张子尧 ||
| v1.0.0|   **3.8.5**  |  CentOS7.6  |rabbitmq集群方案搭建文档 | 2020/12/28 | 张子尧 |使用HAProxy作为集群负载均衡方案|

# 单机环境搭建

> 环境准备：准备一台CentOS7.6的机器。

## 上传安装包到服务器上

**[资源路径](../resources/install/rabbitmq)**

## 安装Erlang语言

因为rabbitmq是基于Erlang语言实现的，所以需要Erlang环境。

```shell
rpm -ivh erlang-18.3-1.el7.centos.x86_64.rpm
```

## 安装rabbitmq

```shell
# 安装
rpm -ivh socat-1.7.3.2-1.1.el7.x86_64.rpm
# 安装
rpm -ivh rabbitmq-server-3.6.5-1.noarch.rpm
```

## 开启界面管理及配置

```shell
#开启界面管理
rabbitmq-plugins enable rabbitmq_management
#修改默认配置
vim /usr/lib/rabbitmq/lib/rabbitmq_server-3.6.5/ebin/rabbit.app
 # loopback_users 中的 <<"guest">>,只保 留guest
```

## 常用命令

```shell
systemctl enable rabbitmq-server  # 开机自启
systemctl start rabbitmq-server  # 启动服务
systemctl restart  rabbitmq-server  # 重启
systemctl stop  rabbitmq-server  # 停止服务
```

## 配置文件路径

```shell
/usr/share/doc/rabbitmq-server-3.6.5/rabbitmq.config.example
#拷贝到 
/etc/rabbitmq/rabbitmq.config
```

## 登录测试

访问出现以下界面则安装成功(默认控制台端口15672，默认用户名密码guest/guest)
![rabbitmq登录界面](images/rabbit登录界面.png)

## 常见问题

1).如果控制台无法访问关闭防火墙或者开放rabbitmq的端口

```shell
#关闭防火墙
systemctl stop firewalld
```

2).如果使用的是云服务器资源则在安全组配置端口开放（具体配置咨询作者）

# 集群环境搭建

## 环境准备

准备三台机器作为rabbitmq集群搭建环境，在准备一台用来搭建HAProxy做负载均衡方案。

```shell
#分别修改三台虚拟机的hostname
hostnamectl set-hostname node1
hostnamectl set-hostname node2
hostnamectl set-hostname node3

#配置hosts
vim /etc/hosts

172.21.0.1 node1
172.21.0.2 node2
172.21.0.3 node2

#设置统一的erlang.cookie中的cookie值
scp /var/lib/rabbitmq/.erlang.cookie node2:/var/lib/rabbitmq/.erlang.cookie
scp /var/lib/rabbitmq/.erlang.cookie node3:/var/lib/rabbitmq/.erlang.cookie

```

## 集群搭建

```shell
#rabbitmq添加节点

#关闭rabbitmq应用
rabbitmqctl stop_app

#node2和node3添加到集群中 分别在node2和node3中执行
rabbitmqctl join_cluster --ram rabbit@node1

#启动应用 
rabbitmqctl start_app

#开启界面管理
rabbitmq-plugins enable rabbitmq_management

#重启rabbitmq服务
systemctl restart rabbitmq-server.service

#查看集群状态
rabbitmqctl cluster_status
```

## 负载均衡-HAProxy

### 简介

HAProxy提供高可用性、负载均衡以及基于TCP和HTTP应用的代理，支持虚拟主机，它是免费、 快速并且可靠的一种解决方案,包括Twitter，Reddit，StackOverflow，GitHub在内的多家知名 互联网公司在使用。HAProxy实现了一种事件驱动、单一进程模型，此模型支持非常大的并发连 接数。

### HAProxy搭建

```shell
#yum源安装proxy
yum install haproxy -y

#修改配置文件 修改内容参考 haproxy.cfg
vim /etc/haproxy/haproxy.cfg

#启动haproxy
systemctl start haproxy.service

#查看haproxy
systemctl status haproxy.service

#查看haproxy监控页面
http://服务器IP:1080/haproxy_stats
```

### HAProxy配置详解
[haproxy.cfg](../resources/conf/rabbitmq/haproxy.cfg)
```shell
global
    log         127.0.0.1 local2
    chroot      /var/lib/haproxy
    pidfile     /var/run/haproxy.pid
    maxconn     4000
    user        haproxy
    group       haproxy
    daemon

    stats socket /var/lib/haproxy/stats

defaults
    mode                    http
    log                     global
    option                  httplog
    option                  dontlognull
    option http-server-close
    option forwardfor       except 127.0.0.0/8
    option                  redispatch
    retries                 3
    timeout http-request    10s
    timeout queue           1m
    timeout connect         10s
    timeout client          1m
    timeout server          1m
    timeout http-keep-alive 10s
    timeout check           10s
    maxconn                 3000


listen rabbitmq_cluster
    bind 0.0.0.0:5672 
     #记录tcp连接的状态和时间
    option tcplog
    #四层协议代理,即对TCP协议转发
    mode tcp
    #开启TCP的Keep Alive. (长连接模式)
    option clitcpka
    #haproxy与mq建立连接的超时时间
    timeout connect 1s
    #客户端与haproxy最大空闲时间。
    timeout client 10s
    #服务器与haproxy最大空闲时间
    timeout server 10s
    #采用轮询转发消息
    balance roundrobin
    #每5秒发送一次心跳包,如连续两次有响应则代表状态良好。
    #如连续三次没有响应,则视为服务故障,该节点将被剔除。
    server node1  192.168.1.1:5672 check inter 5s rise 2 fall 3
    server node2  192.168.1.2:5672 check inter 5s rise 2 fall 3
    server node3  192.168.1.3:5672 check inter 5s rise 2 fall 3

#开启haproxy监控服务
listen http_front
    bind 0.0.0.0:1080
    #统计页面自动刷新时间
    stats refresh 30s
    #统计页面url
    stats uri /haproxy_stats
    #指定HAProxy访问用户名和密码设置
    stats auth admin:admin

```
