SUMMARY = "A library for deep-packet inspection."
DESCRIPTION = "nDPI is an open source LGPLv3 library for deep-packet \
inspection. Based on OpenDPI it includes ntop extensions"

SECTION = "libdevel"
DEPENDS = "libpcap json-c"
RDEPENDS:${PN} += " libpcap"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b52f2d57d10c4f7ee67a7eb9615d5d24"

SRCREV = "8b5c6af71b562549f8416b31803daae223e09f46"
SRC_URI = "git://github.com/ntop/nDPI.git;branch=4.2-stable;protocol=https \
           file://0001-autogen.sh-not-generate-configure.patch \
           "

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig

CPPFLAGS += "${SELECTED_OPTIMIZATION}"

do_configure:prepend() {
    ${S}/autogen.sh
}

EXTRA_OEMAKE = " \
    libdir=${libdir} \
"
