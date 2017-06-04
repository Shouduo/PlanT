#include <OneWire.h>
#include <DallasTemperature.h>
#include <SoftwareSerial.h>
#include <avr/sleep.h>
#include <avr/wdt.h>

#define Reset 2   //smartConfig重置按钮
#define Gpio0 3   //连接8266 
#define Relay 9    //继电器控制脚
#define ONE_WIRE_BUS 8    //DS18B20
#define humSensors A6   //湿度传感
#define brightSensors A7    //光照传感

volatile int cycle = 0;   //时钟中断次数
volatile int trigger = 0;   //0为休眠，1为工作，2为smartConfig
int fails = 0;    //上传数据失败次数

SoftwareSerial softSerial(6, 5);  //RX, TX
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature tempSensors(&oneWire);

void setup() {
    Serial.begin(115200);
    softSerial.begin(115200);
    tempSensors.begin();
    pinMode(humSensors, INPUT);
    pinMode(brightSensors, INPUT);
    pinMode(Reset, OUTPUT);
    pinMode(Gpio0, OUTPUT);
    pinMode(Relay, OUTPUT);

    attachInterrupt(0, start_smartConfig, FALLING);   //D2针脚，终端通道为0
    setup_watchdog(9);  // 0=16ms, 1=32ms, 2=64ms, 3=128ms, 4=250ms, 5=500ms
                        // 6=1sec, 7=2sec, 8=4sec, 9=8sec
    ACSR |= _BV(ACD);  //OFF ACD
//    ADCSRA = 0;   //OFF ADC
    Sleep_avr();  //Sleep_Mode
}

void loop() {
    digitalWrite(Reset, 1);
    switch (trigger) {
        case 1: {
            tempSensors.requestTemperatures();
    
            int humAnalog = analogRead(humSensors);
            int hum = map(humAnalog, 425, 1023, 100, 0);
    
            int brightAnalog = analogRead(brightSensors);
            int bright = 550936 * pow(brightAnalog, -1.893);
    
            int temp = round(tempSensors.getTempCByIndex(0));
    
            String data = (String) "?hum=" + hum + "&bright=" + bright + "&temp=" + temp;
            softSerial.println(data);
            delay(1000);

            String response = "no";
            if (softSerial.available()) {
                response = softSerial.readString();
                delay(100);
            }
            if (response == "ok") {
                trigger = 0;    //结束工作
                fails = 0;    //重置失败次数
                digitalWrite(Relay, 0);   //关断外设继电器
                Sleep_avr();    //进入休眠
            } else {
                fails++;
                if (fails >= 10) {    //上传失败超过10次
                    trigger = 0;
                    fails = 0;
                    digitalWrite(Relay, 0);
                    Sleep_avr();
                }
            }
            break;
        }
        case 2: {
            digitalWrite(Relay, 0);
            digitalWrite(Gpio0, 1);
            delay(500);
            digitalWrite(Relay, 1);
            delay(500);
            digitalWrite(Gpio0, 0);
            delay(60000);
            
//            while (softSerial.available() <= 0) {;}   //等待8266返回数据
//            String state = softSerial.readString();
//            delay(100);
//            if (state == "scdone") {
//                digitalWrite(Gpio0, 1);
//                cycle = 0;    //重新计时
//            } 
            cycle = 0;
            trigger = 0;
            break;
        }
        default: {
            break;
        }
    }
}

void start_smartConfig() {
    trigger = 2;
}

//Sleep mode is activated
void setup_watchdog(int ii) {
    byte bb;
    
    if (ii > 9) ii = 9;
    bb = ii & 7;
    if (ii > 7) bb |= (1 << 5);
    bb |= (1 << WDCE);
    
    MCUSR &= ~(1 << WDRF); 
    WDTCSR |= (1 << WDCE) | (1 << WDE);   // start timed sequence
    WDTCSR = bb;  // set new watchdog timeout value
    WDTCSR |= _BV(WDIE);
}

//WDT interrupt
ISR(WDT_vect) { 
    ++cycle;
    switch (cycle) {
        case 1: {
            digitalWrite(Gpio0, 1);
            digitalWrite(Relay, 1);   //先上电
            break;
        }
        case 2: {
            trigger = 1;
            break;
        }
        case 450: {
            cycle = 0;
            break;
        }
        default: {
            break;
        }
    }
   // wdt_reset();
}
  
void Sleep_avr() {
    set_sleep_mode(SLEEP_MODE_PWR_DOWN); // sleep mode is set here
    sleep_enable();
    sleep_mode();                        // System sleeps here
}

