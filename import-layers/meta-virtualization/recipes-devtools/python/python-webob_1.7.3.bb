DESCRIPTION = "WSGI request and response object"
HOMEPAGE = "http://webob.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/license.txt;md5=8ed3584bcc78c16da363747ccabc5af5"

PYPI_PACKAGE = "WebOb"

SRC_URI[md5sum] = "350028baffc508e3d23c078118e35316"
SRC_URI[sha256sum] = "e65ca14b9f5ae5b031988ffc93f8b7f305ddfcf17a4c774ae0db47bcb3b87283"

inherit setuptools pypi

RDEPENDS_${PN} += " \
	python-sphinx \
	python-nose \
	"

