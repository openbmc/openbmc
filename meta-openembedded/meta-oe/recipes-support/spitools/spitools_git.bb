DESCRIPTION = "This package contains some simple command line tools to help using Linux spidev devices"
HOMEPAGE = "https://github.com/cpb-/spi-tools"
AUTHOR = "Christophe BLAESS"
LICENSE="GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c16666ae6c159876a0ba63099614381"

BPV = "0.8.5"
PV = "${BPV}"
SRCREV = "3c93b9038a7c4d9de4302ab6f14372e9fa381f3c"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/cpb-/spi-tools.git;protocol=git"


inherit autotools
