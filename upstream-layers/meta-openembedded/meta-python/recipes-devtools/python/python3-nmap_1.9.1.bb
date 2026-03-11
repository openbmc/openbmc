DESCRIPTION = "python-nmap is a python library which helps in using nmap port scanner"
HOMEPAGE = "https://www.nmmapper.com/"
SECTION = "devel/python"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

DEPENDS += "python3-wheel-native"

PYPI_PACKAGE = "python3-nmap"

SRC_URI[sha256sum] = "8465cfb013f5cdfa8a1050c40cdae600b581ee32f1864ec404927aee49b4262c"

inherit pypi setuptools3

RDEPENDS:${PN} += "nmap \
	python3-requests \
	python3-simplejson \
"
