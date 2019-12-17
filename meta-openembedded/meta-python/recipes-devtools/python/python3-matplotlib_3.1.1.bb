SUMMARY = "matplotlib: plotting with Python"
DESCRIPTION = "\
Matplotlib is a Python 2D plotting library which produces \
publication-quality figures in a variety of hardcopy formats \
and interactive environments across platforms."
HOMEPAGE = "https://github.com/matplotlib/matplotlib"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE/LICENSE;md5=afec61498aa5f0c45936687da9a53d74"

DEPENDS = "python3-numpy-native python3-numpy freetype libpng python3-dateutil python3-pytz"
RDEPENDS_${PN} = "python3-numpy freetype libpng python3-dateutil python3-pytz"

SRC_URI[md5sum] = "f894af5564a588e880644123237251b7"
SRC_URI[sha256sum] = "1febd22afe1489b13c6749ea059d392c03261b2950d1d45c17e3aed812080c93"

PYPI_PACKAGE = "matplotlib"
inherit pypi setuptools3
