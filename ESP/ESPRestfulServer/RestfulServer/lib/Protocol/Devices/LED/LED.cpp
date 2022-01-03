#include "LED.h"

uint16_t pwmChannels = 0;

void turnOff(LED* led) {
    Serial.println(led->dev.pin);
    if (led->isActiveLow) 
        digitalWrite(led->dev.pin, HIGH);
    else 
        digitalWrite(led->dev.pin, LOW);
    
    led->state = 0;
}

void turnOn(LED* led) {
    Serial.println(led->dev.pin);
    if (led->isActiveLow) 
        digitalWrite(led->dev.pin, LOW);
    else 
        digitalWrite(led->dev.pin, HIGH);

    led->state = 1;
}

void toggle(LED* led) {
    if (led->state) 
        turnOff(led);
    else
        turnOn(led);
}

void blink(void* p) {
    LED* led = (LED*) p;
    for (;;) {
        turnOn(led);
        vTaskDelay((led->bp.onTime*100)/portTICK_PERIOD_MS);

        turnOff(led);
        vTaskDelay((led->bp.offTime*100)/portTICK_PERIOD_MS);
    }
}

// uint8_t freeChannel() {

// }

void dim(void* p) {
}

void dim(LED* led) {
    ledcSetup(led->dp.channel, led->dp.freq, 8);
    ledcAttachPin(led->dev.pin, led->dp.channel);

    if (led->dp.dutyCycle > 100)
        led->dp.dutyCycle = 100;
    else if (led->dp.dutyCycle < 0) 
        led->dp.dutyCycle = 0;

    if (led->isActiveLow)
        led->dp.dutyCycle = 100 - led->dp.dutyCycle;

    int dutyCycle = (int)(((led->dp.dutyCycle*1.0)/100.0)*255);
    ledcWrite(led->dp.channel, dutyCycle);
}

void handleLEDPut(LEDRequests rq, LED* led, uint8_t p1, uint8_t p2) {
    if (led->dev.th != NULL) 
        vTaskDelete(led->dev.th);
    ledcDetachPin(led->dev.pin);

    if (LEDRequests::Blink == rq) {
        led->bp.onTime = p1;
        led->bp.offTime = p2;
        xTaskCreatePinnedToCore(blink, "ledBlinkTaskGroupMember", 550, led, 1, &led->dev.th, 1);   

    } else if (LEDRequests::Dim == rq) {
        led->dp.dutyCycle = p1;
        dim(led);
        // xTaskCreatePinnedToCore(dim, "ledDimTaskGroupMember", 550, led, 1, &led->th, 1);   

    } else if (LEDRequests::TurnOff == rq) {
        Serial.println("Turn off");
        turnOff(led);

    } else if (LEDRequests::TurnOn == rq) {
        Serial.println("Turn on");
        turnOn(led);

    } else if (LEDRequests::Toggle == rq) {
        Serial.println("Toggle");
        toggle(led);
    }
}


void handleLEDPut(LEDRequests rq, LED* led, uint8_t p1, uint8_t p2, String& str) {
    handleLEDPut(rq, led, p1, p2);
    handleLEDGet(LEDRequests::GetState, led, str);
}

void handleLEDGet(LEDRequests rq, LED* led, String& str) {
    StaticJsonDocument<500> doc;
    doc["State"] = (led->state == 0) ? "off" : "on";
    serializeJsonPretty(doc, str);
}

void initLED(LED* led) {
    pinMode(led->dev.pin, OUTPUT);
    turnOff(led);
}