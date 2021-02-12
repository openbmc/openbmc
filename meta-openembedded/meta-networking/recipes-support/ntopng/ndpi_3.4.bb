SUMMARY = "A library for deep-packet inspection."
DESCRIPTION = "nDPI is an open source LGPLv3 library for deep-packet \
inspection. Based on OpenDPI it includes ntop extensions"

SECTION = "libdevel"
DEPENDS = "libpcap"
RDEPENDS_${PN} += " libpcap"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=b52f2d57d10c4f7ee67a7eb9615d5d24"

SRCREV = "64929a75e0a7a60d864bd25a9fd97fdf9ac892a2"
SRC_URI = "git://github.com/ntop/nDPI.git;branch=3.4-stable \
           file://0001-autogen.sh-not-generate-configure.patch \
"

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig

CPPFLAGS += "${SELECTED_OPTIMIZATION}"

do_configure_prepend() {
    ${S}/autogen.sh
}

EXTRA_OEMAKE = " \
    libdir=${libdir} \
"
