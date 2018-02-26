SUMMARY = "Templating library for Python"
HOMEPAGE = "http://www.makotemplates.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1bb21fa2d2f7a534c884b990430a6863"

SRC_URI = "https://files.pythonhosted.org/packages/source/M/Mako/Mako-${PV}.tar.gz"

SRC_URI[md5sum] = "5836cc997b1b773ef389bf6629c30e65"
SRC_URI[sha256sum] = "4e02fde57bd4abb5ec400181e4c314f56ac3e49ba4fb8b0d50bba18cb27d25ae"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/mako/"
UPSTREAM_CHECK_REGEX = "/Mako/(?P<pver>(\d+[\.\-_]*)+)"

S = "${WORKDIR}/Mako-${PV}"

