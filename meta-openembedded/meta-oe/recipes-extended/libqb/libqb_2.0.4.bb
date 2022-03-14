SUMMARY = "An IPC library for high performance servers"
DESCRIPTION = "libqb is a library with the primary purpose of providing high performance client server reusable features. \
It provides high performance logging, tracing, ipc, and poll."

HOMEPAGE = "https://github.com/clusterlabs/libqb/wiki"
SECTION = "libs"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=321bf41f280cf805086dd5a720b37785"

inherit autotools pkgconfig

SRCREV = "a2691b96188033b5ad5c08871982048ae1f4f4e8"
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
