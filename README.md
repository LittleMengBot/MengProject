# MengProject  
## 环境依赖  
Linux Base || macOS x64  
gcc >= 7.5 || clang >= 12.0  （compile required）  
cmake >= 3.13  （compile required）  
libwebp  （compile required）  
libdev  （compile required）  
python >= 3.6  
openjdk-11  
Nginx

## 环境配置  
- 1.&nbsp;安装 [ffmpeg](https://www.ffmpeg.org):  
  macOS:&nbsp;``brew install ffmpeg``  
  Ubuntu:&nbsp;``apt install ffmpeg``  
  centOS:&nbsp;``yum install ffmpeg``
- 2.&nbsp;使用pip3安装 [you-get](https://www.github.com/soimort/you-get)：  
``pip3 install you-get``
- 3.&nbsp;安装并启动 [Tor](https://www.torproject.org/)服务。（侦听端口为9050）
## config.json配置  
```
{  
"admin_id": [], 
"bot_token": "",
"group_id": ,
"corona_headers": {
"x-rapidapi-key": "",
"x-rapidapi-host": ""
},
"speech_api": "https://littlemeng-api.herokuapp.com/",
"python3_path": "/usr/bin/python3",
"youget_path": "/usr/local/bin/you-get",
"ffmpeg_path": "/usr/local/bin/ffmpeg",
"webhook_url": "https://www.example.com/",
"webhook_proxy_port": 5000
}
```
依次填入。  
- ``admin_id`` -> 管理员ID。
- ``group_id`` -> 群组ID。
- ``bot_token`` -> 从 BotFather 处申请。
- ``corona_headers`` -> 从[此处](https://rapidapi.com/api-sports/api/covid-193/)申请。  
- 更改``python3_path``、``youget_path``、``ffmpeg_path``为当前系统对应路径。
- ``webhook_url`` -> 所要设置的webhook地址。  
- ``webhook_proxy_port`` -> Nginx反向代理端口。

### Nginx反向代理配置文件示例：
```
server {
    listen              443 ssl;
    server_name         www.example.com;
    ssl_certificate     /etc/letsencrypt/live/www.example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/www.example.com/privkey.pem;

    location /BOT_TOKEN {
        proxy_pass http://127.0.0.1:5000/BOT_TOKEN;
    }
}
```

## music.json配置  
```
{
  "music": [
    "处处吻",
    "處處吻"
  ],

  "url": [
    "https://www.youtube.com/watch?v=ZMGJEOnuVDw",
    "https://www.youtube.com/watch?v=ZMGJEOnuVDw"
  ]
}
```  
一个音乐标题对应一个链接。为方便搜索建议加入简繁体。  
## 构建  
1.&nbsp;clone此仓库  
2.&nbsp;仓库目录下运行  
```
sudo chmod +x ./gradlew && ./gradlew jar
```
生成的jar包在``build/libs``  
## 安装运行  
首先按照上方步骤进行环境搭建。  
如您的系统为Linux x64，可以直接下载release中的jar文件运行。 
macOS请按照上方流程构建。  
代码中特意加入Windows系统不友好的代码，请自行处理  

直接运行：
```
java -jar xxx.jar
```  
也可配置service文件，示例：  
```
[Unit]
Description=xxxxxx
After=network.target
[Service]
Type=simple
WorkingDirectory=xxxxxx
ExecStart=/usr/bin/java -jar xxx.jar
Restart=always
RestartSec=1
StartLimitInterval=0
[Install]
WantedBy=multi-user.target
```
## 注意  
jar包运行时请确保您已配置好config.json和music.json并放置在jar包同路径下。
## 多语言  
暂时只有中文，您也可以提交pull request进行翻译。
## 其它  
- 采用[Apache-2.0 License](https://www.apache.org/licenses/LICENSE-2.0)  
- 有bug或者建议请在此处提交issue。
## 感谢  
- [fuel](https://github.com/kittinunf/fuel)  *MIT*
- [ktor](https://github.com/ktorio/ktor)  *Apache-2.0*
- [lunar](https://github.com/6tail/lunar-java)  *MIT*
- [jsoup](https://github.com/jhy/jsoup)  *MIT*
- [zxing](https://github.com/zxing/zxing)  *Apache-2.0*
- [rlottie](https://github.com/Samsung/rlottie)  *MIT*  
- [selenium](https://github.com/SeleniumHQ/selenium)  *Apache-2.0*
- [moshi-kotlin](https://github.com/square/moshi)  *Apache-2.0*
- [webp-imageio](https://github.com/sejda-pdf/webp-imageio)  *Apache-2.0*
- [kotlin-telegram-bot](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot)  *Apache-2.0*  
（未列出无License项目。也表示感谢。）