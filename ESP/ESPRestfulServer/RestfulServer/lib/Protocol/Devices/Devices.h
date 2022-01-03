#ifndef DEVICES_H
#define DEVICES_H

#include "LED/LED.h"
#include "LM35/LM35.h"
#include "Photocell/Photocell.h"

typedef struct {
    uint8_t numLEDs;
    LED* leds;

    uint8_t numPhotocells;
    Photocell* photocells;
    
    uint8_t numLM35s;
    LM35* lm35s;

} Devices;

void initDevices(Devices* devs);

#endif