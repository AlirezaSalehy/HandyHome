#ifndef ROUTER_HANDY_HOME_H
#define ROUTER_HANDY_HOME_H

#include <WiFi.h>
#include <AsyncTCP.h>
#include <ESPAsyncWebServer.h>
#include "Description/Description.h"
#include "Devices/Devices.h"

void setupRoutes(AsyncWebServer* server, Devices* dvs);

#endif