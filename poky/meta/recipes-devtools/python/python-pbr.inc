SUMMARY = "Python Build Reasonableness"
DESCRIPTION = "PBR is a library that injects some useful and sensible default behaviors into your setuptools run"
HOMEPAGE = "https://pypi.org/project/pbr"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI += "file://0001-change-shebang-to-python3.patch"

inherit pypi

RDEPENDS:${PN} += "${PYTHON_PN}-pip"

BBCLASSEXTEND = "native nativesdk"
