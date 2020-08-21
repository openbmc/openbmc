SUMMARY = "ALSA bindings"
SECTION = "devel/python"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1a3b161aa0fcec32a0c8907a2219ad9d"

inherit pypi setuptools3

SRC_URI[md5sum] = "48c40424a79c2568676a41643d93f1f7"
SRC_URI[sha256sum] = "3ca069c736c8ad2a3047b5033468d983a2480f94fad4feb0169c056060e01e69"

DEPENDS += "alsa-lib"

RDEPENDS_${PN} += "libasound"
