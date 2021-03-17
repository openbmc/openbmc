#!/bin/bash

serviceName="xyz.openbmc_project.VirtualSensor"
interfaceName="xyz.openbmc_project.Sensor.Value"
objectPath="/xyz/openbmc_project/sensors/temperature/"

# set MaxValue, MinValue to sensor dbus property
for i in {0..2}; do
    sensorPath="${objectPath}i2cool_${i}"
    mapper wait $sensorPath
    busctl set-property $serviceName $sensorPath $interfaceName MaxValue d 127
    busctl set-property $serviceName $sensorPath $interfaceName MinValue d -- -128
done

exit 0
