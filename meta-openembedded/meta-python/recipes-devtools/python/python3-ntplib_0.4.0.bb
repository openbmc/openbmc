DESCRIPTION = "This module offers a simple interface to query NTP servers from Python."
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://ntplib.py;beginline=1;endline=23;md5=afa07338a9595257e94c205c3e72224d"

SRC_URI = "git://github.com/cf-natali/ntplib.git;branch=master;protocol=https"
SRCREV ?= "b9c11c5906bc802a20a2dab390e57c50be558808"

S = "${WORKDIR}/git"

inherit setuptools3 python3native

RDEPENDS:${PN} += "${PYTHON_PN}-datetime ${PYTHON_PN}-io"
