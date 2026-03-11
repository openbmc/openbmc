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

SRC_URI[sha256sum] = "4e460e818629b4b173f32d08bf30d3af8123afbb8e04bb5707a1fd4799e503f0"
