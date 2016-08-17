SUMMARY = "Support for the Linux 2.6.x ALSA Sound System"
SECTION = "devel/python"
DEPENDS = "alsa-lib"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1a3b161aa0fcec32a0c8907a2219ad9d"
SRCNAME = "pyalsaaudio"
PR = "ml2"

SRC_URI = "${SOURCEFORGE_MIRROR}/pyalsaaudio/${SRCNAME}-${PV}.tar.gz"
S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit distutils

SRC_URI[md5sum] = "b312c28efba7db0494836a79f0a49898"
SRC_URI[sha256sum] = "07148ce16024724b17cc24c51d0f4fb78af214b09b7dc8dcb7b06e5647f4c582"
