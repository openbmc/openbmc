DESCRIPTION = "WSGI request and response object"
HOMEPAGE = "http://webob.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/license.txt;md5=8ed3584bcc78c16da363747ccabc5af5"

PR = "r0"
SRCNAME = "WebOb"

SRC_URI = "http://pypi.python.org/packages/source/W/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "089d7fc6745f175737800237c7287802"
SRC_URI[sha256sum] = "63d262d8f61b516321f786879c9277fa2209f7f57eb47b537eeecfea383d55b7"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

RDEPENDS_${PN} += " \
	python-sphinx \
	python-nose \
	"

