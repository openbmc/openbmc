SUMMARY = "Filters to enhance web typography, including support for Django & Jinja templates"
HOMEPAGE = "https://github.com/mintchaos/typogrify"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=12bc792d6f5415dbf421f3fd183c6dd2"

inherit pypi setuptools3

PYPI_PACKAGE = "typogrify"
SRC_URI[sha256sum] = "8be4668cda434163ce229d87ca273a11922cb1614cb359970b7dc96eed13cb38"

BBCLASSEXTEND = "native"

RDEPENDS:${PN} += "${PYTHON_PN}-smartypants"

