SUMMARY = "An IPC library for high performance servers"
DESCRIPTION = "libqb is a library with the primary purpose of providing high performance client server reusable features. \
It provides high performance logging, tracing, ipc, and poll."

HOMEPAGE = "https://github.com/clusterlabs/libqb/wiki"
SECTION = "libs"

LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=321bf41f280cf805086dd5a720b37785"

inherit autotools-brokensep pkgconfig

PV = "0.17.2+git${SRCPV}"

SRCREV = "bd2c587f6ccacd8a5644b275d99324d200c2b378"
SRC_URI = "git://github.com/ClusterLabs/${BPN}.git"

S = "${WORKDIR}/git"

do_configure_prepend() {
    ( cd ${S}
    ${S}/autogen.sh )
}


