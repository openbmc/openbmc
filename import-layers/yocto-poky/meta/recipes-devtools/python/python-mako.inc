SUMMARY = "Templating library for Python"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1bb21fa2d2f7a534c884b990430a6863"

SRC_URI = "https://files.pythonhosted.org/packages/source/M/Mako/Mako-${PV}.tar.gz"

SRC_URI[md5sum] = "a28e22a339080316b2acc352b9ee631c"
SRC_URI[sha256sum] = "48559ebd872a8e77f92005884b3d88ffae552812cdf17db6768e5c3be5ebbe0d"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/mako/"
UPSTREAM_CHECK_REGEX = "/Mako/(?P<pver>(\d+[\.\-_]*)+)"

S = "${WORKDIR}/Mako-${PV}"

