SUMMARY = "An IPC library for high performance servers"
DESCRIPTION = "libqb is a library with the primary purpose of providing high performance client server reusable features. \
It provides high performance logging, tracing, ipc, and poll."

HOMEPAGE = "https://github.com/clusterlabs/libqb/wiki"
SECTION = "libs"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=321bf41f280cf805086dd5a720b37785"

inherit autotools pkgconfig

PV .= "+git${SRCPV}"

SRCREV = "608de6d59a3de0eef0fbcbd8f284acbc018daa9a"
SRC_URI = "git://github.com/ClusterLabs/${BPN}.git \
           file://0001-Remove-runtime-check-for-CLOCK_MONOTONIC.patch \
          "
S = "${WORKDIR}/git"

do_configure_prepend() {
    ( cd ${S}
    ${S}/autogen.sh )
}
