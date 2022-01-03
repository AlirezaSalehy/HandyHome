#ifndef WEB_SERVER_SETUP_H
#define WEB_SERVER_SETUP_H

#include <WiFi.h>
#include <AsyncTCP.h>
#include <ESPAsyncWebServer.h>

#include "Devices/Devices.h"
#include "../Routing/Router.h"

void setupWebServer(char*, char*, int, Devices* devs);
    
static AsyncWebServer server(80);

#endif
