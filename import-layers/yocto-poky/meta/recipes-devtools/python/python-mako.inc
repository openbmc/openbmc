SUMMARY = "Templating library for Python"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1bb21fa2d2f7a534c884b990430a6863"

SRC_URI = "https://files.pythonhosted.org/packages/source/M/Mako/Mako-${PV}.tar.gz"

SRC_URI[md5sum] = "c5fc31a323dd4990683d2f2da02d4e20"
SRC_URI[sha256sum] = "fed99dbe4d0ddb27a33ee4910d8708aca9ef1fe854e668387a9ab9a90cbf9059"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/mako/"
UPSTREAM_CHECK_REGEX = "/Mako/(?P<pver>(\d+[\.\-_]*)+)"

S = "${WORKDIR}/Mako-${PV}"

