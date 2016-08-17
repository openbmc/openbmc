DESCRIPTION = "A microframework based on Werkzeug, Jinja2 and good intentions"
HOMEPAGE = "https://pypi.python.org/pypi/Flask/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=79aa8b7bc4f781210d6b5c06d6424cb0"

PR = "r0"
SRCNAME = "Flask"

SRC_URI = "https://pypi.python.org/packages/source/F/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "378670fe456957eb3c27ddaef60b2b24"
SRC_URI[sha256sum] = "4c83829ff83d408b5e1d4995472265411d2c414112298f2eb4b359d9e4563373"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

CLEANBROKEN = "1"

