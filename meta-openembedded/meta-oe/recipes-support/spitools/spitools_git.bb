DESCRIPTION = "This package contains some simple command line tools to help using Linux spidev devices"
HOMEPAGE = "https://github.com/cpb-/spi-tools"
AUTHOR = "Christophe BLAESS"
LICENSE="GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c16666ae6c159876a0ba63099614381"

BPV = "1.0.0"
PV = "1.0.1"
SRCREV = "87da3bfc03f3088e2e880b6b48195bb225fafeac"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/cpb-/spi-tools.git;protocol=https;branch=master"


inherit autotools
