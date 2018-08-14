DESCRIPTION = "Bluetooth Python extension module"
HOMEPAGE = "http://karulis.github.io/pybluez/"
SECTION = "devel/python"

DEPENDS = "bluez5"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=8a71d0475d08eee76d8b6d0c6dbec543"

inherit pypi

SRC_URI = "https://pypi.python.org/packages/c1/98/3149481d508bee174335be6725880f00d297afebe75c15e917af8f6fe169/PyBluez-0.22.zip"
SRC_URI[md5sum] = "49dab9d5a8f0b798c8125c7f649be3cd"
SRC_URI[sha256sum] = "4ce006716a54d9d18e8186a3f1c8b12a8e6befecffe8fd5828a291fb694ce49d"

S = "${WORKDIR}/PyBluez-${PV}"

RDEPENDS_${PN} += "\
    bluez5 \
    ${PYTHON_PN}-fcntl \
"
