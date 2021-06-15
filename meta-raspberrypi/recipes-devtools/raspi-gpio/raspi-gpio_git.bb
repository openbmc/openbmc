DESCRIPTION = "Tool to help debug / hack at the BCM283x GPIO"
HOMEPAGE = "https://github.com/RPi-Distro/raspi-gpio"
SECTION = "devel/libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a14affa234debc057b47cdca615b2192"

COMPATIBLE_MACHINE = "^rpi$"

inherit autotools

SRCREV = "4edfde183ff3ac9ed66cdc015ae25e45f3a5502d"
SRC_URI = "git://github.com/RPi-Distro/raspi-gpio.git;protocol=https;branch=master \
          "

S = "${WORKDIR}/git"

