SUMMARY = "Python 2 and 3 compatibility library"
HOMEPAGE = "https://pypi.python.org/pypi/six/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6f00d4a50713fa859858dd9abaa35b21"

SRC_URI = "https://files.pythonhosted.org/packages/source/s/six/six-${PV}.tar.gz"
SRC_URI[md5sum] = "34eed507548117b2ab523ab14b2f8b55"
SRC_URI[sha256sum] = "105f8d68616f8248e24bf0e9372ef04d3cc10104f1980f54d57b2ce73a5ad56a"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/six/"
UPSTREAM_CHECK_REGEX = "/six/(?P<pver>(\d+[\.\-_]*)+)"

BBCLASSEXTEND = "native nativesdk"

S = "${WORKDIR}/six-${PV}"
