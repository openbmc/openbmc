DESCRIPTION = "A module to control Raspberry Pi GPIO channels"
HOMEPAGE = "http://code.google.com/p/raspberry-gpio-python/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.txt;md5=9b95630a648966b142f1a0dcea001cb7"

SRCNAME = "RPi.GPIO"

SRC_URI = "\
          http://pypi.python.org/packages/source/R/RPi.GPIO/${SRCNAME}-${PV}.tar.gz \
          file://0001-Remove-nested-functions.patch \
          "
SRC_URI[md5sum] = "254d0443a436eb241367c487274e7197"
SRC_URI[sha256sum] = "54e5fb06d9ea1a1389a497fb5a06dfa950c86303b0f4ba89b68c55999d1df064"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit distutils

COMPATIBLE_MACHINE = "raspberrypi"

