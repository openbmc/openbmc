DESCRIPTION = "A SoCo fork with fixes for Home Assistant "
HOMEPAGE = "https://pypi.org/project/pysonos/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=07b0e2ca9ac77cd65cd4edf2e13367ea"

SRC_URI[sha256sum] = "2a72897cfd342382573632d80d38776884a38c8d02353d9d5da4e9e8e83cb42b"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
	python3-ifaddr \
	python3-requests \
	python3-xmltodict \
	"
