SUMMARY = "A Python Mocking and Patching Library for Testing"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=de9dfbf780446b18aab11f00baaf5b7e"

SRC_URI[md5sum] = "d834a46d9a129be3e76fdcc99751e82c"
SRC_URI[sha256sum] = "83657d894c90d5681d62155c82bda9c1187827525880eda8ff5df4ec813437c3"

inherit pypi setuptools

DEPENDS += " \
    python-pbr-native"

RDEPENDS_${PN} += " \
    python-prettytable \
    python-cmd2 \
    python-pyparsing \
    python-mccabe \
    python-pep8 \
    python-pyflakes \
    python-pbr \
    python-funcsigs \
"
