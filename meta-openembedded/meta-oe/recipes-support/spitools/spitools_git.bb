DESCRIPTION = "This package contains some simple command line tools to help using Linux spidev devices"
HOMEPAGE = "https://github.com/cpb-/spi-tools"
AUTHOR = "Christophe BLAESS"
LICENSE="GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c16666ae6c159876a0ba63099614381"

BPV = "1.0.0"
PV = "${BPV}"
SRCREV = "f5d87954bd9e13689481700105efe2f42199d364"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/cpb-/spi-tools.git;protocol=git"


inherit autotools
