SUMMARY = "Python template engine and code generation tool"
HOMEPAGE = "https://cheetahtemplate.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=15e13a4ed0e5880e3e55ec88b0921181"

PYPI_PACKAGE = "Cheetah3"
inherit pypi setuptools3

RDEPENDS_${PN} = "python3-pickle python3-pprint"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "fc735d4ca7997df2a2da1cae0cf89a1e"
SRC_URI[sha256sum] = "ececc9ca7c58b9a86ce71eb95594c4619949e2a058d2a1af74c7ae8222515eb1"
