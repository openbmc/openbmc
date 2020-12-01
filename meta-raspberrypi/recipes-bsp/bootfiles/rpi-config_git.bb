DESCRIPTION = "Commented config.txt file for the Raspberry Pi. \
               The Raspberry Pi config.txt file is read by the GPU before \
               the ARM core is initialised. It can be used to set various \
               system configuration parameters."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "^rpi$"

SRCREV = "648ffc470824c43eb0d16c485f4c24816b32cd6f"
SRC_URI = "git://github.com/Evilpaul/RPi-config.git;protocol=git;branch=master \
          "

S = "${WORKDIR}/git"

PR = "r5"

INHIBIT_DEFAULT_DEPS = "1"

PITFT="${@bb.utils.contains("MACHINE_FEATURES", "pitft", "1", "0", d)}"
PITFT22="${@bb.utils.contains("MACHINE_FEATURES", "pitft22", "1", "0", d)}"
PITFT28r="${@bb.utils.contains("MACHINE_FEATURES", "pitft28r", "1", "0", d)}"
PITFT28c="${@bb.utils.contains("MACHINE_FEATURES", "pitft28c", "1", "0", d)}"
PITFT35r="${@bb.utils.contains("MACHINE_FEATURES", "pitft35r", "1", "0", d)}"

VC4GRAPHICS="${@bb.utils.contains("MACHINE_FEATURES", "vc4graphics", "1", "0", d)}"
VC4DTBO ?= "vc4-kms-v3d"
GPIO_IR ?= "18"
GPIO_IR_TX ?= "17"

CAN_OSCILLATOR ?= "16000000"

inherit deploy nopackages

do_deploy() {
    install -d ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}

    cp ${S}/config.txt ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/

    if [ -n "${KEY_DECODE_MPG2}" ]; then
        sed -i '/#decode_MPG2=/ c\decode_MPG2=${KEY_DECODE_MPG2}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${KEY_DECODE_WVC1}" ]; then
        sed -i '/#decode_WVC1=/ c\decode_WVC1=${KEY_DECODE_WVC1}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${DISABLE_OVERSCAN}" ]; then
        sed -i '/#disable_overscan=/ c\disable_overscan=${DISABLE_OVERSCAN}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ "${DISABLE_SPLASH}" = "1" ]; then
        sed -i '/#disable_splash=/ c\disable_splash=${DISABLE_SPLASH}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # Set overclocking options
    if [ -n "${ARM_FREQ}" ]; then
        sed -i '/#arm_freq=/ c\arm_freq=${ARM_FREQ}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${GPU_FREQ}" ]; then
        sed -i '/#gpu_freq=/ c\gpu_freq=${GPU_FREQ}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${CORE_FREQ}" ]; then
        sed -i '/#core_freq=/ c\core_freq=${CORE_FREQ}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${SDRAM_FREQ}" ]; then
        sed -i '/#sdram_freq=/ c\sdram_freq=${SDRAM_FREQ}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${OVER_VOLTAGE}" ]; then
        sed -i '/#over_voltage=/ c\over_voltage=${OVER_VOLTAGE}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # GPU memory
    if [ -n "${GPU_MEM}" ]; then
        sed -i '/#gpu_mem=/ c\gpu_mem=${GPU_MEM}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${GPU_MEM_256}" ]; then
        sed -i '/#gpu_mem_256=/ c\gpu_mem_256=${GPU_MEM_256}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${GPU_MEM_512}" ]; then
        sed -i '/#gpu_mem_512=/ c\gpu_mem_512=${GPU_MEM_512}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${GPU_MEM_1024}" ]; then
        sed -i '/#gpu_mem_1024=/ c\gpu_mem_1024=${GPU_MEM_1024}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # Set boot delay
    if [ -n "${BOOT_DELAY}" ]; then
        sed -i '/#boot_delay=/ c\boot_delay=${BOOT_DELAY}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${BOOT_DELAY_MS}" ]; then
        sed -i '/#boot_delay_ms=/ c\boot_delay_ms=${BOOT_DELAY_MS}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # Set HDMI and composite video options
    if [ -n "${HDMI_FORCE_HOTPLUG}" ]; then
        sed -i '/#hdmi_force_hotplug=/ c\hdmi_force_hotplug=${HDMI_FORCE_HOTPLUG}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${HDMI_DRIVE}" ]; then
        sed -i '/#hdmi_drive=/ c\hdmi_drive=${HDMI_DRIVE}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${HDMI_GROUP}" ]; then
        sed -i '/#hdmi_group=/ c\hdmi_group=${HDMI_GROUP}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${HDMI_MODE}" ]; then
        sed -i '/#hdmi_mode=/ c\hdmi_mode=${HDMI_MODE}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${CONFIG_HDMI_BOOST}" ]; then
        sed -i '/#config_hdmi_boost=/ c\config_hdmi_boost=${CONFIG_HDMI_BOOST}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${SDTV_MODE}" ]; then
        sed -i '/#sdtv_mode=/ c\sdtv_mode=${SDTV_MODE}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${SDTV_ASPECT}" ]; then
        sed -i '/#sdtv_aspect=/ c\sdtv_aspect=${SDTV_ASPECT}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ -n "${DISPLAY_ROTATE}" ]; then
        sed -i '/#display_rotate=/ c\display_rotate=${DISPLAY_ROTATE}' ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # Video camera support
    if [ "${VIDEO_CAMERA}" = "1" ]; then
        echo "# Enable video camera" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "start_x=1" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # Offline compositing support
    if [ "${DISPMANX_OFFLINE}" = "1" ]; then
        echo "# Enable offline compositing" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dispmanx_offline=1" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # SPI bus support
    if [ "${ENABLE_SPI_BUS}" = "1" ] || [ "${PITFT}" = "1" ]; then
        echo "# Enable SPI bus" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtparam=spi=on" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # I2C support
    if [ "${ENABLE_I2C}" = "1" ] || [ "${PITFT}" = "1" ]; then
        echo "# Enable I2C" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtparam=i2c1=on" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtparam=i2c_arm=on" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # PiTFT22 display support
    if [ "${PITFT22}" = "1" ]; then
        echo "# Enable PITFT22 display" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtoverlay=pitft22,rotate=270,speed=32000000,txbuflen=32768" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ "${PITFT28r}" = "1" ]; then
        echo "# Enable PITFT28r display" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtoverlay=pitft28-resistive,rotate=90,speed=32000000,txbuflen=32768" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ "${PITFT28c}" = "1" ]; then
        echo "# Enable PITFT28c display" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtoverlay=pitft28-capacitive,rotate=90,speed=32000000,txbuflen=32768" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtoverlay=pitft28-capacitive,touch-swapxy,touch-invx" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi
    if [ "${PITFT35r}" = "1" ]; then
        echo "# Enable PITFT35r display" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtoverlay=pitft35-resistive,rotate=90,speed=42000000,fps=20" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # UART support
    if [ "${ENABLE_UART}" = "1" ]; then
        echo "# Enable UART" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "enable_uart=1" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # Infrared support
    if [ "${ENABLE_IR}" = "1" ]; then
        echo "# Enable infrared" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtoverlay=gpio-ir,gpio_pin=${GPIO_IR}" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtoverlay=gpio-ir-tx,gpio_pin=${GPIO_IR_TX}" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # VC4 Graphics support
    if [ "${VC4GRAPHICS}" = "1" ]; then
        echo "# Enable VC4 Graphics" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtoverlay=${VC4DTBO}" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # Choose Camera Sensor to be used, default imx219 sensor
    if [ "${RASPBERRYPI_CAMERA_V2}" = "1" ]; then
        echo "# Enable Sony RaspberryPi Camera" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtoverlay=imx219" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # Waveshare "C" 1024x600 7" Rev2.1 IPS capacitive touch (http://www.waveshare.com/7inch-HDMI-LCD-C.htm)
    if [ "${WAVESHARE_1024X600_C_2_1}" = "1" ]; then
        echo "# Waveshare \"C\" 1024x600 7\" Rev2.1 IPS capacitive touch screen" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "max_usb_current=1" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "hdmi_group=2" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "hdmi_mode=87" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "hdmi_cvt 1024 600 60 6 0 0 0" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "hdmi_drive=1" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # DWC2 USB peripheral support
    if [ "${ENABLE_DWC2_PERIPHERAL}" = "1" ]; then
        echo "# Enable USB peripheral mode" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtoverlay=dwc2,dr_mode=peripheral" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # AT86RF23X support
    if [ "${ENABLE_AT86RF}" = "1" ]; then
        echo "# Enable AT86RF23X" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtoverlay=at86rf233,speed=3000000" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # ENABLE DUAL CAN
    if [ "${ENABLE_DUAL_CAN}" = "1" ]; then
        echo "# Enable DUAL CAN" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtoverlay=mcp2515-can0,oscillator=${CAN_OSCILLATOR},interrupt=25" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtoverlay=mcp2515-can1,oscillator=${CAN_OSCILLATOR},interrupt=24" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    # ENABLE CAN
    elif [ "${ENABLE_CAN}" = "1" ]; then
        echo "# Enable CAN" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "dtoverlay=mcp2515-can0,oscillator=${CAN_OSCILLATOR},interrupt=25" >>${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    fi

    # Append extra config if the user has provided any
    printf "${RPI_EXTRA_CONFIG}\n" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt

    # Handle setup with armstub file
    if [ "${@bb.utils.contains("MACHINE_FEATURES", "armstub", "1", "0", d)}" = "1" ]; then
        echo "\n# ARM stub configuration" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        echo "armstub=${ARMSTUB}" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
        case "${ARMSTUB}" in
            *-gic.bin)
                echo  "enable_gic=1" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
                ;;
        esac
    fi
}

do_deploy_append_raspberrypi3-64() {
    echo "# have a properly sized image" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "disable_overscan=1" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt

    echo "# Enable audio (loads snd_bcm2835)" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "dtparam=audio=on" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
}

addtask deploy before do_build after do_install
do_deploy[dirs] += "${DEPLOYDIR}/${BOOTFILES_DIR_NAME}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
