DESCRIPTION = "This project enables PWM on the GPIO pins you request of a Raspberry Pi."
HOMEPAGE = "https://github.com/sarfata/pi-blaster/"
SECTION = "devel/libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README.md;beginline=225;endline=252;md5=a012868ef5f83b9f257af253d7cb07a3"

SRC_URI = "git://github.com/sarfata/pi-blaster \
           file://remove-initscript-lsb-dependency.patch \
"

S = "${WORKDIR}/git"

SRCREV = "1035ad7dffb270c40eec1bb3a654171a755fba98"

inherit update-rc.d autotools

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME_${PN} = "${PN}.boot.sh"
INITSCRIPT_PARAMS_${PN} = "defaults 15 85"

COMPATIBLE_MACHINE = "^rpi$"

PACKAGE_ARCH = "${MACHINE_ARCH}"
