DESCRIPTION = "Tool to help debug / hack at the BCM283x GPIO"
HOMEPAGE = "https://github.com/RPi-Distro/raspi-gpio"
SECTION = "devel/libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a14affa234debc057b47cdca615b2192"

COMPATIBLE_MACHINE = "^rpi$"

inherit autotools

SRCREV = "2eaa8b8755a550e34d07c898b90b0d9b3d66fd81"
SRC_URI = "git://github.com/RPi-Distro/raspi-gpio.git;protocol=https;branch=master \
          "

S = "${WORKDIR}/git"

