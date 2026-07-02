SUMMARY = "An IPC library for high performance servers"
DESCRIPTION = "libqb is a library with the primary purpose of providing high performance client server reusable features. \
It provides high performance logging, tracing, ipc, and poll."

HOMEPAGE = "https://github.com/clusterlabs/libqb/wiki"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=321bf41f280cf805086dd5a720b37785"

inherit autotools pkgconfig

SRC_URI = "https://github.com/ClusterLabs/${BPN}/releases/download/v${PV}/${BP}.tar.xz"
SRC_URI[sha256sum] = "326a69fb5b2ee4479f0db4f98d10d670ad0798b5ded8c4cfd585b765fd8941e8"

DEPENDS += "libxml2"

CFLAGS += "-pthread -D_REENTRANT"

BBCLASSEXTEND = "native"
