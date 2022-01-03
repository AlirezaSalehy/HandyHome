#ifndef PHOTOCELL_H
#define PHOTOCELL_H

#include <Arduino.h>
#include <ArduinoJson.h>

#include "Devices/Device.h"

typedef enum {
    readPhotocell = 0
} PhotocellCommand;

typedef struct 
{
    Device dev;

} Photocell;

void handlePhotocellCommands(PhotocellCommand com, Photocell* photocell, String& str);

void initPhotocell(Photocell* photocell);


#endif