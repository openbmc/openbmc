SUMMARY = "Python template engine and code generation tool"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=15e13a4ed0e5880e3e55ec88b0921181"

PYPI_PACKAGE = "Cheetah3"
inherit pypi setuptools3

RDEPENDS_${PN} = "python3-pickle python3-pprint"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "5629445e7fef9659da2b49e36aacdbff"
SRC_URI[sha256sum] = "7c450bce04a82d34cf6d48992c736c2048246cbc00f7b4903a39cf9a8ea3990c"

