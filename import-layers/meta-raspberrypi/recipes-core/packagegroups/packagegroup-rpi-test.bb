DESCRIPTION = "RaspberryPi Test Packagegroup"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit packagegroup

RDEPENDS_${PN} = "\
    omxplayer \
    bcm2835-tests \
    wiringpi \
    rpio \
    rpi-gpio \
    pi-blaster \
    python-rtimu \
    python-sense-hat \
    connman \
    connman-client \
    crda \
    bluez5 \
"

RRECOMMENDS_${PN} = "\
    bigbuckbunny-1080p \
    bigbuckbunny-480p \
    bigbuckbunny-720p \
    ${MACHINE_EXTRA_RRECOMMENDS} \
"
