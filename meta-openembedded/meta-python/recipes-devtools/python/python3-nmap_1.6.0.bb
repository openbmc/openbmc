DESCRIPTION = "python-nmap is a python library which helps in using nmap port scanner"
HOMEPAGE = "https://www.nmmapper.com/"
SECTION = "devel/python"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

DEPENDS += "python3-wheel-native"

PYPI_PACKAGE = "python3-nmap"

SRC_URI[sha256sum] = "892b5091cde429fabfb8ba63382b2db8fd795193ba147558d0a7d5534c956255"

inherit pypi setuptools3

RDEPENDS:${PN} += "nmap \
	python3-requests \
	python3-simplejson \
"
