#!/bin/bash

TARGET_FILE_NAME="/etc/nvme/nvme_config.json"

export_gpio() {
  if [ ! -d "/sys/class/gpio/gpio$1" ]; then
    echo $1 >/sys/class/gpio/export
  fi
}

# Get NVMeDrivePresentPins
# 1-0024

# Get NVMeDrivePwrGoodPins
# 1-0021

if [ -d "/sys/bus/i2c/drivers/pca953x/1-0024" ]; then
    presentPinBase="$(cat /sys/bus/i2c/drivers/pca953x/1-0024/gpio/gpiochip*/base)"
    for i in {0..15};
    do
        let presentPinBase[$i]=presentPinBase+$i
        export_gpio ${presentPinBase[$i]}
    done
else
    echo "Can't find present gpio expander (addr: 0x24) !!"
fi

if [ -d "/sys/bus/i2c/drivers/pca953x/1-0021" ]; then
    PwrGoodPinBase="$(cat /sys/bus/i2c/drivers/pca953x/1-0021/gpio/gpiochip*/base)"
    for i in {0..15};
    do
        let PwrGoodPinBase[$i]=PwrGoodPinBase+$i
        export_gpio ${PwrGoodPinBase[$i]}
    done
else
    echo "Can't find powergood gpio expander (addr: 0x21) !!"
fi

cat > $TARGET_FILE_NAME << EOF1
{
    "config": [
        {
            "NVMeDriveIndex": 0,
            "NVMeDriveBusID": 47,
            "NVMeDriveFaultLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_0_fault",
            "NVMeDriveLocateLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_0_locate",
            "NVMeDriveLocateLEDControllerBusName": "xyz.openbmc_project.LED.Controller.led_u2_0_locate",
            "NVMeDriveLocateLEDControllerPath": "/xyz/openbmc_project/led/physical/led_u2_0_locate",
            "NVMeDrivePresentPin": ${presentPinBase[3]},
            "NVMeDrivePwrGoodPin": ${PwrGoodPinBase[3]}
        },
        {
            "NVMeDriveIndex": 1,
            "NVMeDriveBusID": 46,
            "NVMeDriveFaultLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_1_fault",
            "NVMeDriveLocateLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_1_locate",
            "NVMeDriveLocateLEDControllerBusName": "xyz.openbmc_project.LED.Controller.led_u2_1_locate",
            "NVMeDriveLocateLEDControllerPath": "/xyz/openbmc_project/led/physical/led_u2_1_locate",
            "NVMeDrivePresentPin": ${presentPinBase[2]},
            "NVMeDrivePwrGoodPin": ${PwrGoodPinBase[2]}
        },
        {
            "NVMeDriveIndex": 2,
            "NVMeDriveBusID": 45,
            "NVMeDriveFaultLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_2_fault",
            "NVMeDriveLocateLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_2_locate",
            "NVMeDriveLocateLEDControllerBusName": "xyz.openbmc_project.LED.Controller.led_u2_2_locate",
            "NVMeDriveLocateLEDControllerPath": "/xyz/openbmc_project/led/physical/led_u2_2_locate",
            "NVMeDrivePresentPin": ${presentPinBase[1]},
            "NVMeDrivePwrGoodPin": ${PwrGoodPinBase[1]}
        },
        {
            "NVMeDriveIndex": 3,
            "NVMeDriveBusID": 44,
            "NVMeDriveFaultLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_3_fault",
            "NVMeDriveLocateLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_3_locate",
            "NVMeDriveLocateLEDControllerBusName": "xyz.openbmc_project.LED.Controller.led_u2_3_locate",
            "NVMeDriveLocateLEDControllerPath": "/xyz/openbmc_project/led/physical/led_u2_3_locate",
            "NVMeDrivePresentPin": ${presentPinBase[0]},
            "NVMeDrivePwrGoodPin": ${PwrGoodPinBase[0]}
        },
        {
            "NVMeDriveIndex": 4,
            "NVMeDriveBusID": 39,
            "NVMeDriveFaultLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_4_fault",
            "NVMeDriveLocateLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_4_locate",
            "NVMeDriveLocateLEDControllerBusName": "xyz.openbmc_project.LED.Controller.led_u2_4_locate",
            "NVMeDriveLocateLEDControllerPath": "/xyz/openbmc_project/led/physical/led_u2_4_locate",
            "NVMeDrivePresentPin": ${presentPinBase[7]},
            "NVMeDrivePwrGoodPin": ${PwrGoodPinBase[7]}
        },
        {
            "NVMeDriveIndex": 5,
            "NVMeDriveBusID": 38,
            "NVMeDriveFaultLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_5_fault",
            "NVMeDriveLocateLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_5_locate",
            "NVMeDriveLocateLEDControllerBusName": "xyz.openbmc_project.LED.Controller.led_u2_5_locate",
            "NVMeDriveLocateLEDControllerPath": "/xyz/openbmc_project/led/physical/led_u2_5_locate",
            "NVMeDrivePresentPin": ${presentPinBase[6]},
            "NVMeDrivePwrGoodPin": ${PwrGoodPinBase[6]}
        },
        {
            "NVMeDriveIndex": 6,
            "NVMeDriveBusID": 37,
            "NVMeDriveFaultLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_6_fault",
            "NVMeDriveLocateLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_6_locate",
            "NVMeDriveLocateLEDControllerBusName": "xyz.openbmc_project.LED.Controller.led_u2_6_locate",
            "NVMeDriveLocateLEDControllerPath": "/xyz/openbmc_project/led/physical/led_u2_6_locate",
            "NVMeDrivePresentPin": ${presentPinBase[5]},
            "NVMeDrivePwrGoodPin": ${PwrGoodPinBase[5]}
        },
        {
            "NVMeDriveIndex": 7,
            "NVMeDriveBusID": 36,
            "NVMeDriveFaultLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_7_fault",
            "NVMeDriveLocateLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_7_locate",
            "NVMeDriveLocateLEDControllerBusName": "xyz.openbmc_project.LED.Controller.led_u2_7_locate",
            "NVMeDriveLocateLEDControllerPath": "/xyz/openbmc_project/led/physical/led_u2_7_locate",
            "NVMeDrivePresentPin": ${presentPinBase[4]},
            "NVMeDrivePwrGoodPin": ${PwrGoodPinBase[4]}
        },
        {
            "NVMeDriveIndex": 8,
            "NVMeDriveBusID": 31,
            "NVMeDriveFaultLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_8_fault",
            "NVMeDriveLocateLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_8_locate",
            "NVMeDriveLocateLEDControllerBusName": "xyz.openbmc_project.LED.Controller.led_u2_8_locate",
            "NVMeDriveLocateLEDControllerPath": "/xyz/openbmc_project/led/physical/led_u2_8_locate",
            "NVMeDrivePresentPin": ${presentPinBase[11]},
            "NVMeDrivePwrGoodPin": ${PwrGoodPinBase[11]}
        },
        {
            "NVMeDriveIndex": 9,
            "NVMeDriveBusID": 30,
            "NVMeDriveFaultLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_9_fault",
            "NVMeDriveLocateLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_9_locate",
            "NVMeDriveLocateLEDControllerBusName": "xyz.openbmc_project.LED.Controller.led_u2_9_locate",
            "NVMeDriveLocateLEDControllerPath": "/xyz/openbmc_project/led/physical/led_u2_9_locate",
            "NVMeDrivePresentPin": ${presentPinBase[10]},
            "NVMeDrivePwrGoodPin": ${PwrGoodPinBase[10]}
        },
        {
            "NVMeDriveIndex": 10,
            "NVMeDriveBusID": 29,
            "NVMeDriveFaultLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_10_fault",
            "NVMeDriveLocateLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_10_locate",
            "NVMeDriveLocateLEDControllerBusName": "xyz.openbmc_project.LED.Controller.led_u2_10_locate",
            "NVMeDriveLocateLEDControllerPath": "/xyz/openbmc_project/led/physical/led_u2_10_locate",
            "NVMeDrivePresentPin": ${presentPinBase[9]},
            "NVMeDrivePwrGoodPin": ${PwrGoodPinBase[9]}
        },
        {
            "NVMeDriveIndex": 11,
            "NVMeDriveBusID": 28,
            "NVMeDriveFaultLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_11_fault",
            "NVMeDriveLocateLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_11_locate",
            "NVMeDriveLocateLEDControllerBusName": "xyz.openbmc_project.LED.Controller.led_u2_11_locate",
            "NVMeDriveLocateLEDControllerPath": "/xyz/openbmc_project/led/physical/led_u2_11_locate",
            "NVMeDrivePresentPin": ${presentPinBase[8]},
            "NVMeDrivePwrGoodPin": ${PwrGoodPinBase[8]}
        },
        {
            "NVMeDriveIndex": 12,
            "NVMeDriveBusID": 27,
            "NVMeDriveFaultLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_12_fault",
            "NVMeDriveLocateLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_12_locate",
            "NVMeDriveLocateLEDControllerBusName": "xyz.openbmc_project.LED.Controller.led_u2_12_locate",
            "NVMeDriveLocateLEDControllerPath": "/xyz/openbmc_project/led/physical/led_u2_12_locate",
            "NVMeDrivePresentPin": ${presentPinBase[15]},
            "NVMeDrivePwrGoodPin": ${PwrGoodPinBase[15]}
        },
        {
            "NVMeDriveIndex": 13,
            "NVMeDriveBusID": 26,
            "NVMeDriveFaultLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_13_fault",
            "NVMeDriveLocateLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_13_locate",
            "NVMeDriveLocateLEDControllerBusName": "xyz.openbmc_project.LED.Controller.led_u2_13_locate",
            "NVMeDriveLocateLEDControllerPath": "/xyz/openbmc_project/led/physical/led_u2_13_locate",
            "NVMeDrivePresentPin": ${presentPinBase[14]},
            "NVMeDrivePwrGoodPin": ${PwrGoodPinBase[14]}
        },
        {
            "NVMeDriveIndex": 14,
            "NVMeDriveBusID": 25,
            "NVMeDriveFaultLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_14_fault",
            "NVMeDriveLocateLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_14_locate",
            "NVMeDriveLocateLEDControllerBusName": "xyz.openbmc_project.LED.Controller.led_u2_14_locate",
            "NVMeDriveLocateLEDControllerPath": "/xyz/openbmc_project/led/physical/led_u2_14_locate",
            "NVMeDrivePresentPin": ${presentPinBase[13]},
            "NVMeDrivePwrGoodPin": ${PwrGoodPinBase[13]}
        },
        {
            "NVMeDriveIndex": 15,
            "NVMeDriveBusID": 24,
            "NVMeDriveFaultLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_15_fault",
            "NVMeDriveLocateLEDGroupPath": "/xyz/openbmc_project/led/groups/led_u2_15_locate",
            "NVMeDriveLocateLEDControllerBusName": "xyz.openbmc_project.LED.Controller.led_u2_15_locate",
            "NVMeDriveLocateLEDControllerPath": "/xyz/openbmc_project/led/physical/led_u2_15_locate",
            "NVMeDrivePresentPin": ${presentPinBase[12]},
            "NVMeDrivePwrGoodPin": ${PwrGoodPinBase[12]}
        }
    ],
    "threshold": [
        {
            "criticalHigh": 77,
            "criticalLow": 0,
            "warningHigh": 77,
            "warningLow": 0,
            "maxValue": 127,
            "minValue": -128
        }
    ]
}
EOF1
