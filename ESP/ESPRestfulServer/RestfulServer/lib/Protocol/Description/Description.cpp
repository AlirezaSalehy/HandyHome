#include "Description.h"

void generalDescription(JsonObject& obj, Device& dev) {
  obj["location"] = dev.location;
  obj["description"] = dev.desc;
  obj["type"] = DeviceTypeDesc[dev.type];
}

void getDescription(String& str, Devices* dvs) {
    DynamicJsonDocument doc(1024);
    JsonArray devices = doc.createNestedArray("Devices");

    for (uint8_t i = 0; i < dvs->numLEDs; i++) {
        JsonObject temp = devices.createNestedObject();
        generalDescription(temp, dvs->leds[i].dev);
        temp["id"] = i;
        temp["url"] = "/led";
        temp["commands"] = "/led";
    }

    for (uint8_t i = 0; i < dvs->numPhotocells; i++) {
        JsonObject temp = devices.createNestedObject();
        generalDescription(temp, dvs->photocells[i].dev);
        temp["id"] = i;
        temp["url"] = "/photocell";
        temp["commands"] = "/photocell";
    }

    for (uint8_t i = 0; i < dvs->numLM35s; i++) {
        JsonObject temp = devices.createNestedObject();
        generalDescription(temp, dvs->lm35s[i].dev);
        temp["id"] = i;
        temp["url"] = "/lm35";
        temp["commands"] = "/lm35";
    }

    serializeJsonPretty(doc, str);
}


/**
 * 
{
  "devices": [
      {
        "type": "Temperature Sensor",
        "location": "Dinning Room",
        "custom_description": "",
        "value": 35,
        "unit": "Celcius",
        "url": "ts1"
      }, 
      {
        "type": "Lamp",
        "location": "Living Room",
        "custom_description": "",
        "value": "off",
        "unit": "enum(on,off)",      
        "url": "lp1"
      }, 
      {
        "type": "Lamp",
        "location": "Kitchen",
        "custom_description": "",
        "value": "on",
        "unit": "enum(on,off)",
        "url": "lp2"

      }, 
      {
        "type": "Light Sensor",
        "location": "Bed Room",
        "custom_description": "",
        "value": 35,
        "unit": "Celcius",
        "url": "/ls1"
      }
    ]
}
 * 
 */