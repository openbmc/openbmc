DESCRIPTION = "This package contains some simple command line tools to help using Linux spidev devices"
HOMEPAGE = "https://github.com/cpb-/spi-tools"
AUTHOR = "Christophe BLAESS"
LICENSE="GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c16666ae6c159876a0ba63099614381"

BPV = "0.8.3"
PV = "${BPV}"
SRCREV = "a3f1f68a048d0d2321ee562b3744dd1162cad22f"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/cpb-/spi-tools.git;protocol=git"


inherit autotools
