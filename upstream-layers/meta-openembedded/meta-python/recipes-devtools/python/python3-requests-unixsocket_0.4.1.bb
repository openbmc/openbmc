SUMMARY = "Use requests to talk HTTP via a UNIX domain socket"
HOMEPAGE = "https://pypi.org/project/requests-unixsocket/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

SRC_URI[sha256sum] = "b2596158c356ecee68d27ba469a52211230ac6fb0cde8b66afb19f0ed47a1995"

PYPI_PACKAGE = "requests_unixsocket"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-pbr-native python3-setuptools-scm-native"
RDEPENDS:${PN} = "python3-requests python3-urllib3"
