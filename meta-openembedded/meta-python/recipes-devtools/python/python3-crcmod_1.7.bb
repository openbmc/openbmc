SUMMARY = "A Python module for generating objects that compute the Cyclic Redundancy Check."
HOMEPAGE = "https://pypi.org/project/crcmod"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f9a19291627cad2d1dfbfcf3c9fb85c2"

SRC_URI[md5sum] = "2d5b92117d958dcead94f9e17f54cd32"
SRC_URI[sha256sum] = "dc7051a0db5f2bd48665a990d3ec1cc305a466a77358ca4492826f41f283601e"

inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-unittest"

BBCLASSEXTEND = "native nativesdk"
