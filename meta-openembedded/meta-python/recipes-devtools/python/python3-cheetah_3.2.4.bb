SUMMARY = "Python template engine and code generation tool"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=15e13a4ed0e5880e3e55ec88b0921181"

PYPI_PACKAGE = "Cheetah3"
inherit pypi setuptools3

RDEPENDS_${PN} = "python3-pickle python3-pprint"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "8c0ac643263ffc3454fb321342284d0a"
SRC_URI[sha256sum] = "caabb9c22961a3413ac85cd1e5525ec9ca80daeba6555f4f60802b6c256e252b"
