#ifndef LED_H
#define LED_H

#include <Arduino.h>
#include <ArduinoJson.h>
#include "Devices/Device.h"

typedef enum
{
    TurnOn = 0,
    TurnOff,
    Toggle,
    Blink,
    Dim,
    GetState
} LEDRequests;

typedef struct {
    uint8_t onTime;
    uint8_t offTime;

} BlinkParams;

typedef struct {
    int dutyCycle;
    uint8_t channel;
    uint32_t freq;

} DimParams;

typedef struct
{
    Device dev;

    bool isActiveLow;
    uint8_t state;
    BlinkParams bp;
    DimParams dp;

} LED;

void handleLEDPut(LEDRequests rq, LED* led, uint8_t p1, uint8_t p2, String& str);
void handleLEDPut(LEDRequests rq, LED* led, uint8_t p1, uint8_t p2);
void handleLEDGet(LEDRequests rq, LED* led, String& str);

void initLED(LED* led);

#endif