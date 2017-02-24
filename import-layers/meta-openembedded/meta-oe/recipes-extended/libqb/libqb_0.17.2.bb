SUMMARY = "An IPC library for high performance servers"
DESCRIPTION = "libqb is a library with the primary purpose of providing high performance client server reusable features. \
It provides high performance logging, tracing, ipc, and poll."

HOMEPAGE = "https://github.com/clusterlabs/libqb/wiki"

SECTION = "libs"

inherit autotools pkgconfig

SRC_URI = "https://fedorahosted.org/releases/q/u/quarterback/${BP}.tar.xz \
          "

SRC_URI[md5sum] = "de1e5d38fa449b4d127940c10d117260"
SRC_URI[sha256sum] = "9a419c649ed51f275dc780da8a15babb8a5d33633567bd9e0cb6193b6e21f4fe"

LICENSE = "LGPL-2.1"

LIC_FILES_CHKSUM = "file://COPYING;md5=321bf41f280cf805086dd5a720b37785"

do_configure_prepend() {
    ( cd ${S}
    ${S}/autogen.sh )
}


