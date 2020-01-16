SUMMARY = "Command line interface for testing internet bandwidth using speedtest.net"
AUTHOR = "Matt Martz"

LICENSE="Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit setuptools3

SRC_URI = "git://github.com/sivel/speedtest-cli.git"
SRCREV = "c58ad3367bf27f4b4a4d5b1bca29ebd574731c5d"

S = "${WORKDIR}/git"

RDEPENDS_${PN} = "python3 python3-setuptools-scm"
