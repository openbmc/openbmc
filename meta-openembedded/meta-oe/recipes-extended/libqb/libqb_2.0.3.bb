SUMMARY = "An IPC library for high performance servers"
DESCRIPTION = "libqb is a library with the primary purpose of providing high performance client server reusable features. \
It provides high performance logging, tracing, ipc, and poll."

HOMEPAGE = "https://github.com/clusterlabs/libqb/wiki"
SECTION = "libs"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=321bf41f280cf805086dd5a720b37785"

inherit autotools pkgconfig

SRCREV = "404adbcd998ec83643e47d92b3ea8d9c3970e68b"
SRC_URI = "git://github.com/ClusterLabs/${BPN}.git;branch=master;protocol=https \
          "
S = "${WORKDIR}/git"

DEPENDS += "libxml2"

CFLAGS += "-pthread -D_REENTRANT"

do_configure:prepend() {
    ( cd ${S}
    ${S}/autogen.sh )
}

BBCLASSEXTEND = "native"
