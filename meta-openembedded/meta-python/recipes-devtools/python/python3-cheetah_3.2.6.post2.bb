SUMMARY = "Python template engine and code generation tool"
HOMEPAGE = "https://cheetahtemplate.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=15e13a4ed0e5880e3e55ec88b0921181"

PYPI_PACKAGE = "Cheetah3"
inherit pypi setuptools3

RDEPENDS:${PN} = "python3-pickle python3-pprint"
RDEPENDS:${PN}:class-native = ""

BBCLASSEXTEND = "native nativesdk"

SRC_URI[sha256sum] = "63157d7a00a273b59676b5be5aa817c75c37efc88478231f1a160f4cfb7f7878"
