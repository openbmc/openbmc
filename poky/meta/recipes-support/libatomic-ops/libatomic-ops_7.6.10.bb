SUMMARY = "A library for atomic integer operations"
HOMEPAGE = "https://github.com/ivmai/libatomic_ops/"
SECTION = "optional"
PROVIDES += "libatomics-ops"
LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://doc/LICENSING.txt;md5=e00dd5c8ac03a14c5ae5225a4525fa2d \
                    "

SRC_URI = "https://github.com/ivmai/libatomic_ops/releases/download/v${PV}/libatomic_ops-${PV}.tar.gz"
UPSTREAM_CHECK_URI = "https://github.com/ivmai/libatomic_ops/releases"

SRC_URI[md5sum] = "90a78a84d9c28ce11f331c25289bfbd0"
SRC_URI[sha256sum] = "587edf60817f56daf1e1ab38a4b3c729b8e846ff67b4f62a6157183708f099af"

S = "${WORKDIR}/libatomic_ops-${PV}"

ALLOW_EMPTY_${PN} = "1"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
