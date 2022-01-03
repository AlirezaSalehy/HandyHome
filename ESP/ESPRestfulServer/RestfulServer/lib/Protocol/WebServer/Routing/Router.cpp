#include "Router.h"

void notFound(AsyncWebServerRequest *request) {
    request->send(404, "text/plain", "Not found");
}

void handleLEDRoutes(AsyncWebServerRequest *request, Devices* dvs) {
    bool parseSuccess = false;
    String js;

    if (request->hasParam("id", false) && request->hasParam("request", false)) {
        uint8_t id = request->getParam("id", false)->value().toInt();
        uint8_t reqType = request->getParam("request", false)->value().toInt();

        if (id < dvs->numLEDs) {
            if (request->method() == HTTP_GET) 
                handleLEDGet((LEDRequests) reqType, &(dvs->leds[id]), js);
            else if (request->method() == HTTP_PUT) {
                uint8_t p1 = 0, p2 = 0;                    
                if (reqType == LEDRequests::Blink) {
                    if (request->hasParam("onTime", false) && request->hasParam("offTime", false)) {
                        p1 =  request->getParam("onTime", false)->value().toInt();
                        p2 =  request->getParam("offTime", false)->value().toInt();
                        parseSuccess = true;
                    }
                } else if (reqType == LEDRequests::Dim) {
                    if (request->hasParam("dutyCycle", false)) {
                        p1 =  request->getParam("dutyCycle", false)->value().toInt();
                        parseSuccess = true;
                    }
                } else {
                    parseSuccess = true;
                }

                if (parseSuccess == true)
                    handleLEDPut((LEDRequests) reqType, &(dvs->leds[id]), p1, p2, js);
            }
        }
    }

            
    if (parseSuccess) 
        request->send(200, "text/plain", js);
    else
        request->send(400, "text/plain", "Invalid");
}

void handlePhotocellRoutes(AsyncWebServerRequest *request, Devices* dvs) { 
    String js;
    if (request->hasParam("id", false) && request->hasParam("request", false)) {
        uint8_t id = request->getParam("id", false)->value().toInt();
        uint8_t reqType = request->getParam("request", false)->value().toInt();

        if (id < dvs->numLEDs) {
            if (request->method() == HTTP_GET) {
                handlePhotocellCommands((PhotocellCommand)reqType, &(dvs->photocells[id]), js);
            }
            
            if (request->method() == HTTP_GET) {
                request->send(200, "text/plain", js);
                return;
            }
        }
    }

    request->send(400, "text/plain", "Invalid");
}

void handleLM35Routes(AsyncWebServerRequest *request, Devices* dvs) {
    String js;
    if (request->hasParam("id", false) && request->hasParam("request", false)) {
        uint8_t id = request->getParam("id", false)->value().toInt();
        uint8_t reqType = request->getParam("request", false)->value().toInt();

        if (id < dvs->numLEDs) {
            if (request->method() == HTTP_GET) {
                handleLM35Commands((LM35Command)reqType, &(dvs->lm35s[id]), js);
            }
            
            if (request->method() == HTTP_GET) {
                request->send(200, "text/plain", js);
                return;
            }
        }
    }

    request->send(400, "text/plain", "Invalid");
 }

void handleDescriptionRequest(AsyncWebServerRequest *request, Devices* dvs) {
    String js;
    getDescription(js, dvs);
    request->send(200, "text/plain", js);
}

void setupRoutes(AsyncWebServer* server, Devices* dvs) {

    server->on("/led", [dvs](AsyncWebServerRequest *request){
        handleLEDRoutes(request, dvs);
    });

    server->on("/photocell", [dvs](AsyncWebServerRequest *request){
        handlePhotocellRoutes(request, dvs);
    });

    server->on("/lm35", [dvs](AsyncWebServerRequest *request){
        handleLM35Routes(request, dvs);
    });

    server->on("/desc", HTTP_GET, [dvs](AsyncWebServerRequest *request){
        handleDescriptionRequest(request, dvs);
    });

    server->onNotFound(notFound);
}