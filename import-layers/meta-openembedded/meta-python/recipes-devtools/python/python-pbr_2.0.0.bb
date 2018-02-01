DESCRIPTION = "Python Build Reasonableness: PBR is a library that injects some useful and sensible default behaviors into your setuptools run"
HOMEPAGE = "https://pypi.python.org/pypi/pbr"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI[md5sum] = "dfc1c3788eff06acfaade6f1655fa490"
SRC_URI[sha256sum] = "0ccd2db529afd070df815b1521f01401d43de03941170f8a800e7531faba265d"

inherit pypi setuptools

RDEPENDS_${PN}_class-target += " \
        python-pip \
        "

BBCLASSEXTEND = "native"
