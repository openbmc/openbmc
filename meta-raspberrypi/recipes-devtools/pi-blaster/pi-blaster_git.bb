DESCRIPTION = "This project enables PWM on the GPIO pins you request of a Raspberry Pi."
HOMEPAGE = "https://github.com/sarfata/pi-blaster/"
SECTION = "devel/libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README.md;beginline=268;endline=292;md5=86d10e4bcf4b4014d306dde7c1d2a80d"

SRC_URI = "git://github.com/sarfata/pi-blaster \
           file://remove-initscript-lsb-dependency.patch \
           "

S = "${WORKDIR}/git"

SRCREV = "befd8ef36e5066e4d444ef47fe4020787e541248"

inherit update-rc.d autotools

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME_${PN} = "${PN}.boot.sh"
INITSCRIPT_PARAMS_${PN} = "defaults 15 85"

COMPATIBLE_MACHINE = "^rpi$"

PACKAGE_ARCH = "${MACHINE_ARCH}"
