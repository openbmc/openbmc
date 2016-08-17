SUMMARY = "Library for interfacing with common SoC peripherals"
DESCRIPTION = "libsoc is a C library to interface with common peripherals (gpio, i2c, spi, pwm) \
               found in SoC (System on Chips) through generic Linux Kernel interfaces."

HOMEPAGE = "https://github.com/jackmitch/libsoc"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=e0bfebea12a718922225ba987b2126a5"

inherit autotools

SRCREV = "2919c35e706fe7c311f83f9343865420fa4b37fe"
SRC_URI = "git://github.com/jackmitch/libsoc.git"

S = "${WORKDIR}/git"

RDEPENDS_${PN} = "libgcc"
