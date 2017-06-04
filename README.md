# PlanT
PlanT是一款智能家居产品，主要功能是能够帮助用户感知和监控影响盆栽生长状态的三个环境因素，即土壤湿度、环境光照和环境温度。

整个系统主要由：花盆及内嵌的下位机、Web服务器、手机App三部分组成。

1. 花盆由普通花盆和藏匿于内部的下位机组成，下位机主控选用Arduino Pro Mini，配备湿度传感器、温度传感器、光照传感器等传感器，用于采集环境数据，主控处理后通过WiFi模块上传至服务器。
2. Web服务器用于数据存储和转发，在PC上部署Apach-Tomcat作为容器，安装使用MySQL数据库对接收到的数据进行存储管理，编写servlet响应处理来自App和下位机的http请求。
3. 手机终端App用于与用户进行交互，App的后台服务开启时周期性向服务器发送请求，将返回的数据解析后存储至本地，并在活动中将数据转化为更加直观的图表展现给用户，后台服务也会在土壤湿度过低等情况下直接向用户发送通知。

![img](/Img/flowSteps.png "flow steps")

## 花盆及下位机

* 每小时轮询更新，待机时休眠并断开外设供电
* 使用ESP8266官方提供的SmartConfig SDK进行快速WiFi连接配置

![img](/Img/pot.png "smart pot")

![img](/Img/pcb.png "pcb")

## Android客户端
* 交互设计遵循 Material Design 规范
* 使用SmartConfig配置WiFi连接
* 可以设置发起系统通知的条件和免打扰时间

![img](/Img/mainActivity.png "Main Activity")

![img](/Img/smartConfig.png "Smart Config")

![img](/Img/otherActivities.png "Main Activity")

##Q&A

* Q：为何项目名为'PlanT'  
A：一是 'Plant' 一词有 '植物、盆栽' 的意思,故意大写的 'T' 使得项目名也可被理解为 'Plan T',意为 'T计划' 或 'T项目',希望人们可以像开发维护项目那般细心地关注自己的小 'T' (形似植株的枝干和叶片)


* Q：为何设计云端服务器  
A：因为考虑到花盆下位机应该具备良好的可移动性和电池续航能力，无法做到随时接收和响应客户端发起的请求，故设计云端服务器作为数据中介，同时也可避免因下位机意外断电导致的数据丢失。

* Q：为何不加入自动浇灌功能  
A：因为这是一个民用级的产品，目标用户是办公室上班族、普通家庭主妇、校园学生党等将种植花草作为环境改善、闲暇消遣或情感寄托的人群。对于他们来说自动浇灌功能并非劳动力的解放，反而可能剥夺了种植的乐趣。其次，考虑到产品的美观性，自动浇灌装置所需的蓄水箱也难以安置进容积在200ml左右、摆放在桌面上或是窗台前的花盆中。再者，蓄水箱的引入也可能带来蚊虫滋生的问题，这显然不是人们在办公环境和家庭等场所希望看到的。


## License
```
  ```
     Copyright (C) 2017 Shouduo
  
     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at
  
         http://www.apache.org/licenses/LICENSE-2.0
  
     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
  ```
```