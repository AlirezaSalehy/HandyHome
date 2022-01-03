#ifndef CONFIG_ESP32_SERVER_H
#define CONFIG_ESP32_SERVER_H

#include "Devices/Devices.h"
#include "Devices/Device.h"


LED leds[] = {
    {{25, DeviceType::Light, "Small Red LED", "Kitchen", NULL}, true, 0, {0, 0}, {0, 0, 5000}},
    {{33, DeviceType::Light, "Small Blue LED", "Kitchen", NULL}, true, 0, {0, 0}, {0, 1, 5000}},
    {{32, DeviceType::Light, "Big Red LED", "Kitchen", NULL}, true, 0, {0, 0}, {0, 2, 5000}}
};

Photocell photocells[] {
    {{ 35, DeviceType::LightSensor, "Photocell Light Sensor", "Bed Room", NULL}}
};

LM35 lm35s[] = {
    {{ 34, DeviceType::TemperatureSensor, "LM35 Temperature Sensor", "اتاق خواب", NULL}}
};

static Devices devs = {
    sizeof(leds)/sizeof(LED),
    leds,
    sizeof(photocells)/sizeof(Photocell),
    photocells,   
    sizeof(lm35s)/sizeof(LM35),
    lm35s
};

#endif