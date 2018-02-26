DESCRIPTION = "matplotlib is a python 2D plotting library which produces publication quality figures in a variety of hardcopy formats"
SECTION = "devel/python"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://LICENSE/LICENSE;md5=afec61498aa5f0c45936687da9a53d74"

DEPENDS += "python-numpy freetype libpng python-dateutil python-pytz"
RDEPENDS_${PN} = "python-core python-distutils python-numpy freetype libpng python-dateutil python-pytz"

SRC_URI = "https://github.com/matplotlib/matplotlib/archive/v${PV}.tar.gz \
           file://fix_setupext.patch \
"
SRC_URI[md5sum] = "89717c1ef3c6fdcd6fb1f3b597a4858c"
SRC_URI[sha256sum] = "aebed23921562792b68b8ca355de5abc176af4424f1987e2fa95f65e5c5e7e89"
S = "${WORKDIR}/matplotlib-${PV}"
EXTRA_OECONF = "--disable-docs"

inherit setuptools pkgconfig

