//
// A simple server implementation showing how to:
//  * serve static messages
//  * read GET and POST parameters
//  * handle missing pages / 404s
//

#include <Arduino.h>

#include "wifi_config.h"
#include "device_config.h"

#include "WebServer/Setup/WebServerSetup.h"

void setupWiFi();

void setup() {
    Serial.begin(115200);

    initDevices(&devs);
    setupWebServer((char*)ssid, (char*)password, 80, &devs);
}

void loop() {
}


