SUMMARY = "Python Build Reasonableness"
DESCRIPTION = "PBR is a library that injects some useful and sensible default behaviors into your setuptools run"
HOMEPAGE = "https://pypi.org/project/pbr"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

DEPENDS = "python3-setuptools-native"

inherit pypi python_pbr

RDEPENDS:${PN} += "python3-setuptools"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[sha256sum] = "66ece3a2f5c4f05e5f37569213e755193f6e00b8aa361bfb190533ea8ee8b10c"
