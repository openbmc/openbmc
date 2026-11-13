DESCRIPTION = "This package contains some simple command line tools to help using Linux spidev devices"
HOMEPAGE = "https://github.com/cpb-/spi-tools"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c16666ae6c159876a0ba63099614381"

SRCREV = "57a950d2853aeac3e69c4f79873c0ab9930e4f7f"

SRC_URI = "git://github.com/cpb-/spi-tools.git;protocol=https;branch=master;tag=${PV}"

inherit autotools
