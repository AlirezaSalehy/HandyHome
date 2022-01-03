#include "Photocell.h"

void handlePhotocellCommands(PhotocellCommand com, Photocell* photocell, String& str) {
   uint16_t ADCResult = analogRead(photocell->dev.pin);
   StaticJsonDocument<500> doc;

   uint16_t max = 4095;
   uint16_t min = 350;
   if (ADCResult < min)
      ADCResult = min;
   
   doc["lightnessPercentage"] = (1-((ADCResult*1.0-min)/(max-350)))*100;
   doc["rawADC"] = ADCResult;
   serializeJsonPretty(doc, str);
}

void initPhotocell(Photocell* photocell) {
   pinMode(photocell->dev.pin, INPUT);
}