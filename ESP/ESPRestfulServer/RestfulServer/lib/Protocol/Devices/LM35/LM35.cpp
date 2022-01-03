#include "LM35.h"

void handleLM35Commands(LM35Command com, LM35* lm35, String& str) {
    uint16_t ADCResult = analogRead(lm35->dev.pin);
    double voltage = (ADCResult*1.0/4095) * 3.3;

    StaticJsonDocument<500> doc;
    doc["voltageMV"] = voltage*1000;
    doc["temp"] = voltage*100;
    serializeJsonPretty(doc, str);
}

void initLM35(LM35* lm35) {
    pinMode(lm35->dev.pin, INPUT);
}