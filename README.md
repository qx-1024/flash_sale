# 基于SpringCloud+Vue的分布式秒杀系统

## 一、环境准备

本地环境：

1、安装 IDEA

2、安装 Maven

3、安装 Tomcat

4、安装 Nacos【单节点】

安装目录：E:/nacos

5、安装 MySQL【单节点】

虚拟机环境：

1、安装 Nginx【主备集群，开机自启动】

2、安装 Redis【单节点】

安装目录：/usr/local/src/redis-4.0.0

3、安装 RabbitMQ【单节点，开机自启动】

4、安装 Minio【单节点】

安装目录：/usr/local/src/minio_linux



## 二、启动项目

前端：

```bash
npm run dev
```

后端：

直接 IDEA 运行（每个服务在三个不同的端口启动三次即可）



启动各个组件：

Nacos：

```bash
.\start.cmd
```

Redis：

```bash
cd /usr/local/src/redis-4.0.0/src
redis-server ../redis-conf
```

Minio：

```bash
cd /usr/local/src/minio_linux
MINIO_ROOT_USER=admin MINIO_ROOT_PASSWORD=password ./minio server ./data --console-address :9001 &
```

