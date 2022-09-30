SUMMARY = "Use requests to talk HTTP via a UNIX domain socket"
HOMEPAGE = "https://pypi.org/project/requests-unixsocket/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

SRC_URI[sha256sum] = "28304283ea9357d45fff58ad5b11e47708cfbf5806817aa59b2a363228ee971e"

PYPI_PACKAGE = "requests-unixsocket"

inherit pypi
inherit setuptools3

DEPENDS += "python3-pbr-native"
RDEPENDS:${PN} = "python3-requests python3-urllib3"
