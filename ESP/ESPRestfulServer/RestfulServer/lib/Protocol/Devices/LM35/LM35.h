#ifndef LM35_H
#define LM35_H

#include <Arduino.h>
#include <ArduinoJson.h>

#include "Devices/Device.h"

typedef enum {
    readLM35 = 0
} LM35Command;

typedef struct 
{
    Device dev;

} LM35;

void handleLM35Commands(LM35Command com, LM35* lm35, String& str);

void initLM35(LM35* lm35);

#endif