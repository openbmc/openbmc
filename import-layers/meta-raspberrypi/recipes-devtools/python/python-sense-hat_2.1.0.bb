SUMMARY = "Python module to control the Raspberry Pi Sense HAT used in the Astro Pi mission"
HOMEPAGE = "https://github.com/RPi-Distro/python-sense-hat"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENCE.txt;md5=d80fe312e1ff5fbd97369b093bf21cda"

SRCNAME = "sense-hat"

SRC_URI = "https://pypi.python.org/packages/source/s/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "71217f15ea963040f06e2f50722186ca"
SRC_URI[sha256sum] = "c6c76707c0ea514e4b0f1f96f1b5b79755875891aae037df7434b6aad7b9dbca"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

DEPENDS += " \
    jpeg \
    zlib \
    freetype \
    "

RDEPENDS_${PN} += " \
    python-numpy \
    python-rtimu \
    python-imaging \
    "
