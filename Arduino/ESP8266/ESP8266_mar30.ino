#include <ESP8266WiFi.h>
#define Light 2
#define Reset 0

//const char *ssid = "JieBaoWiFi640"; //连接的wifi ssid
//const char *password = "91726313";  //连接的wifi密码
const char *host = "192.168.123.1"; //Server服务端的IP地址
const int tcpPort = 8080; //Server服务端的端口号

String update(String str);
void smartConfig();

void setup() {
    Serial.begin(115200);
    pinMode(Light, OUTPUT);
    pinMode(Reset, INPUT);
//    digitalWrite(Light, 1);
    delay(1000);
    if (!digitalRead(Reset)) {
        smartConfig();
    }
}

void loop() {
    String data;
    String response;
    while(Serial.available()) {
        data = Serial.readString();
    }
    if (data.length() > 0) {
        response = update(data);
        Serial.print(response);
        delay(100);
    }
}

String update(String str) {
    WiFiClient client;
    while (!client.connected()) { //几个非连接的异常处理
        if (!client.connect(host, tcpPort)) {
            delay(100);
        }
    }
    
    int length = str.length();
    String postRequest = (String) "POST " + "/PlanT/Update"
                                + str
                                + " HTTP/1.1" + "\r\n"
                                + "Host: " + host + ":" + tcpPort + "\r\n"
                                + "Content-Length: " + length + "\r\n"
                                + "Connection: " + "close" + "\r\n"
                                + "\r\n";
    
    client.print(postRequest);
    delay(100);
    String response = "nu";
    if (client.available()) {
        response = client.readString();
        delay(100);
        response = response.substring(response.length() - 2, response.length());
    }
    client.stop();
    return response;
}

void smartConfig() {
    WiFi.mode(WIFI_STA);
    WiFi.beginSmartConfig();
    while (1) {
        digitalWrite(Light, 0);
        delay(250);
        digitalWrite(Light, 1);
        delay(250);
        if (WiFi.smartConfigDone()) {
            Serial.print("scdone");
            delay(1000);
            break;
        }
    }
}

