SUMMARY = "A library for deep-packet inspection."
DESCRIPTION = "nDPI is an open source LGPLv3 library for deep-packet \
inspection. Based on OpenDPI it includes ntop extensions"

SECTION = "libdevel"
DEPENDS = "libpcap json-c"
RDEPENDS:${PN} += " libpcap"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b52f2d57d10c4f7ee67a7eb9615d5d24"

SRCREV = "375f99ef9fb4999d778b57bbeece171b3fa9fba6"
SRC_URI = "git://github.com/ntop/nDPI.git;branch=5.0-stable;protocol=https;tag=${PV} \
           file://0001-configure-Stop-embedding-pwd-at-configure-time.patch \
          "

inherit autotools pkgconfig

CPPFLAGS += "${SELECTED_OPTIMIZATION}"

do_configure:prepend() {
    (cd ${S} && ${S}/autogen.sh)
}

do_install:append() {
    install -Dm 0644 ${B}/src/include/ndpi_define.h ${D}${includedir}/ndpi/ndpi_define.h
}

EXTRA_OEMAKE = " \
    libdir=${libdir} \
"
