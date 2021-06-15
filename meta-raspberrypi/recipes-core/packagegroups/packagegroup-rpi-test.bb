DESCRIPTION = "RaspberryPi Test Packagegroup"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

COMPATIBLE_MACHINE = "^rpi$"

OMXPLAYER  = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', 'omxplayer', d)}"

RDEPENDS_${PN} = "\
    ${OMXPLAYER} \
    bcm2835-tests \
    rpio \
    rpi-gpio \
    pi-blaster \
    python3-rtimu \
    connman \
    connman-client \
    wireless-regdb-static \
    bluez5 \
"

RRECOMMENDS_${PN} = "\
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-multimedia", "bigbuckbunny-1080p bigbuckbunny-480p bigbuckbunny-720p", "", d)} \
    ${MACHINE_EXTRA_RRECOMMENDS} \
"
