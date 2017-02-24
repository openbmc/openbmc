DESCRIPTION = "Python Build Reasonableness: PBR is a library that injects some useful and sensible default behaviors into your setuptools run"
HOMEPAGE = "https://pypi.python.org/pypi/pbr"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI[md5sum] = "8e4968c587268f030e38329feb9c8f17"
SRC_URI[sha256sum] = "186428c270309e6fdfe2d5ab0949ab21ae5f7dea831eab96701b86bd666af39c"

inherit pypi setuptools

DEPENDS_class-target += " \
        python-pip \
        "

BBCLASSEXTEND = "native"
