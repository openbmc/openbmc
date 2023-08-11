SUMMARY = "An IPC library for high performance servers"
DESCRIPTION = "libqb is a library with the primary purpose of providing high performance client server reusable features. \
It provides high performance logging, tracing, ipc, and poll."

HOMEPAGE = "https://github.com/clusterlabs/libqb/wiki"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=321bf41f280cf805086dd5a720b37785"

inherit autotools pkgconfig

SRCREV = "002171bbcf4bc4728da56c1538afd9e9d814ecaf"
SRC_URI = "git://github.com/ClusterLabs/${BPN}.git;branch=main;protocol=https \
          "
S = "${WORKDIR}/git"

DEPENDS += "libxml2"

CFLAGS += "-pthread -D_REENTRANT"

do_configure:prepend() {
    ( cd ${S}
    ${S}/autogen.sh )
}

BBCLASSEXTEND = "native"
