#ifndef HANDY_HOME_DEVICE_H
#define HANDY_HOME_DEVICE_H

typedef enum {
    Light = 0,
    TemperatureSensor,
    LightSensor,

} DeviceType;

const String DeviceTypeDesc[] = {{"Light"}, {"TemperatureSensor"}, {"LightSensor"}};

typedef struct {
    uint8_t pin;
    
    DeviceType type;
    String desc;
    String location;

    TaskHandle_t th;

} Device;

#endif