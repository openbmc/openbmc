DESCRIPTION = "A SoCo fork with fixes for Home Assistant "
HOMEPAGE = "https://pypi.org/project/pysonos/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=07b0e2ca9ac77cd65cd4edf2e13367ea"

SRC_URI[sha256sum] = "e24ea33f4ab810ff5e698d5e577d382413a2994565439ebc67fb42614f899e93"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
	${PYTHON_PN}-ifaddr \
	${PYTHON_PN}-requests \
	${PYTHON_PN}-xmltodict \
	"
