SUMMARY = "Python template engine and code generation tool"
HOMEPAGE = "https://cheetahtemplate.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=15e13a4ed0e5880e3e55ec88b0921181"

PYPI_PACKAGE = "Cheetah3"
inherit pypi setuptools3

RDEPENDS_${PN} = "python3-pickle python3-pprint"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"

SRC_URI[sha256sum] = "f1c2b693cdcac2ded2823d363f8459ae785261e61c128d68464c8781dba0466b"
