SUMMARY = "Command line interface for testing internet bandwidth using speedtest.net"
HOMEPAGE = "https://github.com/sivel/speedtest-cli"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[md5sum] = "543d38f8939e1716641cc7c00169ca03"
SRC_URI[sha256sum] = "cf1d386222f94c324e3125ba9a0d187e46d4a13dca08c023bdb9a23096be2e54"

inherit pypi setuptools3

RDEPENDS_${PN} = "${PYTHON_PN}-misc"
