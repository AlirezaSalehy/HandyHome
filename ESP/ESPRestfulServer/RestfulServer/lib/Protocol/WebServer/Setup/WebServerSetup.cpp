#include "WebServerSetup.h"

void setupWiFi(char* ssid, char* password) {
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  if (WiFi.waitForConnectResult() != WL_CONNECTED) {
      Serial.printf("WiFi Failed!\n");
      return;
  }
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());
}


void setupWebServer(char* ssid, char* password, int, Devices* devs) {
    setupWiFi(ssid, password);

    setupRoutes(&server, devs);

    server.begin();
}


