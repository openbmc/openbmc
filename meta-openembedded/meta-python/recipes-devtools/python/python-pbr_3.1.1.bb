DESCRIPTION = "Python Build Reasonableness: PBR is a library that injects some useful and sensible default behaviors into your setuptools run"
HOMEPAGE = "https://pypi.python.org/pypi/pbr"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

SRC_URI[md5sum] = "4e82c2e07af544c56a5b71c801525b00"
SRC_URI[sha256sum] = "05f61c71aaefc02d8e37c0a3eeb9815ff526ea28b3b76324769e6158d7f95be1"

inherit pypi setuptools

RDEPENDS_${PN}_class-target += " \
        python-pip \
        "

BBCLASSEXTEND = "native"
