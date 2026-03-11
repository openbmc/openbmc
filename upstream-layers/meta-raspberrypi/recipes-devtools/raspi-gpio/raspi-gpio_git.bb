DESCRIPTION = "Tool to help debug / hack at the BCM283x GPIO"
HOMEPAGE = "https://github.com/RPi-Distro/raspi-gpio"
SECTION = "devel/libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a14affa234debc057b47cdca615b2192"

COMPATIBLE_MACHINE = "^rpi$"

inherit autotools

SRCREV = "22b44e4765b4b78dc5b22394fff484e353d5914d"
SRC_URI = "git://github.com/RPi-Distro/raspi-gpio.git;protocol=https;branch=master \
          "

