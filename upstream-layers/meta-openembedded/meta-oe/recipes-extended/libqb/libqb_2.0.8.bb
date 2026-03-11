SUMMARY = "An IPC library for high performance servers"
DESCRIPTION = "libqb is a library with the primary purpose of providing high performance client server reusable features. \
It provides high performance logging, tracing, ipc, and poll."

HOMEPAGE = "https://github.com/clusterlabs/libqb/wiki"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=321bf41f280cf805086dd5a720b37785"

inherit autotools pkgconfig

SRC_URI = "https://github.com/ClusterLabs/${BPN}/releases/download/v${PV}/${BP}.tar.xz"
SRC_URI[sha256sum] = "b42531fc20b8ac02f4c6d0a4dc49f7c4a1eef09bdb13af5f6927b7fc49522ee6"

DEPENDS += "libxml2"

CFLAGS += "-pthread -D_REENTRANT"

BBCLASSEXTEND = "native"
