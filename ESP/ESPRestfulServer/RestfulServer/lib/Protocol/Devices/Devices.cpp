#include "Devices.h"

void initDevices(Devices* devs) {
    for (uint8_t i = 0; i < devs->numLEDs; i++)
        initLED(&(devs->leds[i]));
    

    for (uint8_t i = 0; i < devs->numPhotocells; i++) 
        initPhotocell(&(devs->photocells[i]));


    for (uint8_t i = 0; i < devs->numLM35s; i++) 
        initLM35(&(devs->lm35s[i]));
}
