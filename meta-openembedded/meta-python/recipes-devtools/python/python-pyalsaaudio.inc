SUMMARY = "Support for the Linux 2.6.x ALSA Sound System"
SECTION = "devel/python"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1a3b161aa0fcec32a0c8907a2219ad9d"

SRC_URI[md5sum] = "b46f69561bc85fc52e698b2440ca251e"
SRC_URI[sha256sum] = "84e8f8da544d7f4bd96479ce4a237600077984d9be1d7f16c1d9a492ecf50085"

DEPENDS += "alsa-lib"

RDEPENDS_${PN} += "libasound"
