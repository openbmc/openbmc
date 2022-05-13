echo "reload sensor config $1"

systemctl stop xyz.openbmc_project.Logging.IPMI.service
if [ $1 = "1" ]; then
    if [ ! -d "/sys/bus/platform/drivers/nuvoton-i2c/f0086000.i2c" ]; then
        echo -n "f0086000.i2c" > /sys/bus/platform/drivers/nuvoton-i2c/bind
    fi
    sleep 10
else
    if [ -d "/sys/bus/platform/drivers/nuvoton-i2c/f0086000.i2c" ]; then
        echo -n "f0086000.i2c" > /sys/bus/platform/drivers/nuvoton-i2c/unbind
    fi
    sleep 10
fi
systemctl restart xyz.openbmc_project.psusensor.service
systemctl start xyz.openbmc_project.Logging.IPMI.service
