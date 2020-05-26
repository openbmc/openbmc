DESCRIPTION = "This module offers a simple interface to query NTP servers from Python."
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://ntplib.py;beginline=1;endline=23;md5=afa07338a9595257e94c205c3e72224d"

SRCNAME = "ntplib"
SRC_URI[md5sum] = "c7cc8e9b09f40c84819859d70b7784ca"
SRC_URI[sha256sum] = "c4621b64d50be9461d9bd9a71ba0b4af06fbbf818bbd483752d95c1a4e273ede"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools3 python3native pypi

RDEPENDS_${PN} += "${PYTHON_PN}-datetime ${PYTHON_PN}-io"
