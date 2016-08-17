DESCRIPTION = "Commented config.txt file for the Raspberry Pi. \
               The Raspberry Pi config.txt file is read by the GPU before \
               the ARM core is initialised. It can be used to set various \
               system configuration parameters."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

COMPATIBLE_MACHINE = "raspberrypi"

SRCREV = "648ffc470824c43eb0d16c485f4c24816b32cd6f"
SRC_URI = "git://github.com/Evilpaul/RPi-config.git;protocol=git;branch=master \
          "

S = "${WORKDIR}/git"

PR = "r4"

PITFT="${@bb.utils.contains("MACHINE_FEATURES", "pitft", "1", "0", d)}"
PITFT22="${@bb.utils.contains("MACHINE_FEATURES", "pitft22", "1", "0", d)}"
PITFT28r="${@bb.utils.contains("MACHINE_FEATURES", "pitft28r", "1", "0", d)}"

inherit deploy

do_deploy() {
    install -d ${DEPLOYDIR}/bcm2835-bootfiles

    cp ${S}/config.txt ${DEPLOYDIR}/bcm2835-bootfiles/

    if [ -n "${KEY_DECODE_MPG2}" ]; then
        sed -i '/#decode_MPG2/ c\decode_MPG2=${KEY_DECODE_MPG2}' ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi
    if [ -n "${KEY_DECODE_WVC1}" ]; then
        sed -i '/#decode_WVC1/ c\decode_WVC1=${KEY_DECODE_WVC1}' ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi
    if [ -n "${DISABLE_OVERSCAN}" ]; then
        sed -i '/#disable_overscan/ c\disable_overscan=${DISABLE_OVERSCAN}' ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi
    if [ -n "${ARM_FREQ}" ]; then
        sed -i '/#arm_freq/ c\arm_freq=${ARM_FREQ}' ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi
    if [ -n "${CORE_FREQ}" ]; then
        sed -i '/#core_freq/ c\core_freq=${CORE_FREQ}' ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi
    if [ -n "${SDRAM_FREQ}" ]; then
        sed -i '/#sdram_freq/ c\sdram_freq=${SDRAM_FREQ}' ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi
    if [ -n "${OVER_VOLTAGE}" ]; then
        sed -i '/#over_voltage/ c\over_voltage=${OVER_VOLTAGE}' ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi

    # GPU memory
    if [ -n "${GPU_MEM}" ]; then
        sed -i '/#gpu_mem=/ c\gpu_mem=${GPU_MEM}' ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi
    if [ -n "${GPU_MEM_256}" ]; then
        sed -i '/#gpu_mem_256/ c\gpu_mem_256=${GPU_MEM_256}' ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi
    if [ -n "${GPU_MEM_512}" ]; then
        sed -i '/#gpu_mem_512/ c\gpu_mem_512=${GPU_MEM_512}' ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi
    if [ -n "${GPU_MEM_1024}" ]; then
        sed -i '/#gpu_mem_1024/ c\gpu_mem_1024=${GPU_MEM_1024}' ${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi

    # Video camera support
    if [ -n "${VIDEO_CAMERA}" ]; then
        echo "# Enable video camera" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
        echo "start_x=1" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi

    # Offline compositing support
    if [ -n "${DISPMANX_OFFLINE}" ]; then
        echo "# Enable offline compositing" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
        echo "dispmanx_offline=1" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi

    # SPI bus support
    if [ -n "${ENABLE_SPI_BUS}" ] || [ "${PITFT}" = "1" ]; then
        echo "# Enable SPI bus" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
        echo "dtparam=spi=on" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi

    if [ -n "${ENABLE_I2C}" ] || [ "${PITFT}" = "1" ]; then
        echo "# Enable I2C" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
        echo "dtparam=i2c1=on" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
        echo "dtparam=i2c_arm=on" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi

    # PiTFT22 display support
    if [ "${PITFT22}" = "1" ]; then
        echo "# Enable PITFT22 display" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
        echo "dtoverlay=pitft22,rotate=270,speed=32000000,txbuflen=32768" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi

    if [ "${PITFT28r}" = "1" ]; then
        echo "# Enable PITFT28r display" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
        echo "dtoverlay=pitft28-resistive,rotate=90,speed=32000000,txbuflen=32768" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi

    # UART support
    if [ "${ENABLE_UART}" = "1" ]; then
        echo "# Enable UART" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
        echo "enable_uart=1" >>${DEPLOYDIR}/bcm2835-bootfiles/config.txt
    fi
}

addtask deploy before do_package after do_install
do_deploy[dirs] += "${DEPLOYDIR}/bcm2835-bootfiles"

PACKAGE_ARCH = "${MACHINE_ARCH}"
