SUMMARY = "Command line interface for testing internet bandwidth using speedtest.net"
AUTHOR = "Matt Martz"

LICENSE="Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit setuptools3

SRC_URI = "git://github.com/sivel/speedtest-cli.git;branch=master;protocol=https"
SRCREV = "42e96b13dda2afabbcec2622612d13495a415caa"

S = "${WORKDIR}/git"

RDEPENDS:${PN} = "python3 python3-setuptools-scm"
