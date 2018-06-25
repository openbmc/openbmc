SUMMARY = "An IPC library for high performance servers"
DESCRIPTION = "libqb is a library with the primary purpose of providing high performance client server reusable features. \
It provides high performance logging, tracing, ipc, and poll."

HOMEPAGE = "https://github.com/clusterlabs/libqb/wiki"
SECTION = "libs"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=321bf41f280cf805086dd5a720b37785"

inherit autotools pkgconfig

PV .= "+git${SRCPV}"

# v1.0.3
SRCREV = "28dff090c74b6ba8609c4797294a5afe3fe73987"
SRC_URI = "git://github.com/ClusterLabs/${BPN}.git \
           file://0001-build-fix-configure-script-neglecting-re-enable-out-.patch \
          "
S = "${WORKDIR}/git"

CFLAGS += "-pthread -D_REENTRANT"
do_configure_prepend() {
    ( cd ${S}
    ${S}/autogen.sh )
}
