DESCRIPTION = "This module offers a simple interface to query NTP servers from Python."
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://ntplib.py;beginline=1;endline=23;md5=afa07338a9595257e94c205c3e72224d"

SRC_URI = "git://github.com/cf-natali/ntplib.git"
SRCREV ?= "aea7925c26152024ca8cf207e77f403f8127727a"

S = "${WORKDIR}/git"

inherit setuptools3 python3native

RDEPENDS_${PN} += "${PYTHON_PN}-datetime ${PYTHON_PN}-io"
