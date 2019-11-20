SUMMARY = "C library and tools for interacting with the linux GPIO character device"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2caced0b25dfefd4c601d92bd15116de"

SRC_URI = "https://www.kernel.org/pub/software/libs/libgpiod/${BP}.tar.xz"

inherit autotools pkgconfig

PACKAGECONFIG[tests] = "--enable-tests,--disable-tests,kmod udev"

EXTRA_OECONF = "--enable-tools"

PACKAGES =+ " ${PN}-tools"

FILES_${PN}-tools = "${bindir}/*"
