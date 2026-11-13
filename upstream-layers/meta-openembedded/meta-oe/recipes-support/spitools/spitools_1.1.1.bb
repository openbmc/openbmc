DESCRIPTION = "This package contains some simple command line tools to help using Linux spidev devices"
HOMEPAGE = "https://github.com/cpb-/spi-tools"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c16666ae6c159876a0ba63099614381"

SRCREV = "74ba65b19e0cf9d1eabd52d17d842200ef901d61"


SRC_URI = "git://github.com/cpb-/spi-tools.git;protocol=https;branch=master;tag=${PV}"


inherit autotools
