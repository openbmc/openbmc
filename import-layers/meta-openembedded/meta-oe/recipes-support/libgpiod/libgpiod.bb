SUMMARY = "C library and tools for interacting with the linux GPIO character device"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2caced0b25dfefd4c601d92bd15116de"

SRC_URI = "https://www.kernel.org/pub/software/libs/libgpiod/${BP}.tar.xz"

SRC_URI[md5sum] = "5f9d855352b1a5272cf6f1c2e20689d2"
SRC_URI[sha256sum] = "6dfd6aeb544e8b7baf484a05c9ae0e67022c109362a41d87005393046b6beacf"

PV = "0.3.1"

inherit autotools pkgconfig

# enable tools
PACKAGECONFIG ?= "tools"

PACKAGECONFIG[tests] = "--enable-tests,--disable-tests,kmod udev"
PACKAGECONFIG[tools] = "--enable-tools,--disable-tools,"

PACKAGES += " ${PN}-tools"

FILES_${PN}-tools = "${bindir}/*"
