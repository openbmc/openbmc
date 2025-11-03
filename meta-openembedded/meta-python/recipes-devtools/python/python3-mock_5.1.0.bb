DESCRIPTION = "A Python Mocking and Patching Library for Testing"
HOMEPAGE = "https://pypi.python.org/pypi/mock"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=de9dfbf780446b18aab11f00baaf5b7e"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-cmd2 \
    python3-mccabe \
    python3-pep8 \
    python3-prettytable \
    python3-pyflakes \
    python3-pyparsing \
    python3-unittest \
"

SRC_URI[sha256sum] = "5e96aad5ccda4718e0a229ed94b2024df75cc2d55575ba5762d31f5767b8767d"
