SUMMARY = "An IPC library for high performance servers"
DESCRIPTION = "libqb is a library with the primary purpose of providing high performance client server reusable features. \
It provides high performance logging, tracing, ipc, and poll."

HOMEPAGE = "https://github.com/clusterlabs/libqb/wiki"

SECTION = "libs"

inherit autotools pkgconfig

SRC_URI = "https://fedorahosted.org/releases/q/u/quarterback/${BP}.tar.xz \
          "

SRC_URI[md5sum] = "5770b343baa4528f6fec90120ec55048"
SRC_URI[sha256sum] = "7a2115f83bfe20eaa5f2e4ed235e8f2994235d3b87e3e5ca41ba47b320f12e29"

LICENSE = "LGPL-2.1"

LIC_FILES_CHKSUM = "file://COPYING;md5=321bf41f280cf805086dd5a720b37785"

do_configure_prepend() {
    ( cd ${S}
    ${S}/autogen.sh )
}


