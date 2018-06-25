DESCRIPTION = "This package contains some simple command line tools to help using Linux spidev devices"
HOMEPAGE = "https://github.com/cpb-/spi-tools"
AUTHOR = "Christophe BLAESS"
LICENSE="GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c16666ae6c159876a0ba63099614381"

PR = "r0"

BPV = "0.8.1"
PV = "${BPV}"
SRCREV = "318bcae5249722873bf58b27afdd20473c7047cc"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/cpb-/spi-tools.git;protocol=git"


inherit autotools

