SUMMARY = "Command line interface for testing internet bandwidth using speedtest.net"
HOMEPAGE = "https://github.com/sivel/speedtest-cli"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "5e2773233cedb5fa3d8120eb7f97bcc4974b5221b254d33ff16e2f1d413d90f0"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-misc \
    python3-threading \
    python3-xml \
"
