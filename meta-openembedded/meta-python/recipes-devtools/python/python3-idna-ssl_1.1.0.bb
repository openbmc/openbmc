SUMMARY = "Patch ssl.match_hostname for Unicode(idna) domains support"
HOMEPAGE = "https://github.com/aio-libs/idna-ssl"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a61b9c5aec8796b64a6bf15d42605073"

SRC_URI[sha256sum] = "a933e3bb13da54383f9e8f35dc4f9cb9eb9b3b78c6b36f311254d6d0d92c6c7c"

PYPI_PACKAGE = "idna-ssl"
inherit pypi setuptools3

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/idna_ssl/"
UPSTREAM_CHECK_REGEX = "/idna_ssl/(?P<pver>(\d+[\.\-_]*)+)"

RDEPENDS:${PN} += " \
    python3-idna \
    python3-io \
"
