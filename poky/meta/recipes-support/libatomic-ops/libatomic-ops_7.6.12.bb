SUMMARY = "A library for atomic integer operations"
DESCRIPTION = "Package provides semi-portable access to hardware-provided atomic memory update operations on a number of architectures."
HOMEPAGE = "https://github.com/ivmai/libatomic_ops/"
SECTION = "optional"
PROVIDES += "libatomics-ops"
LICENSE = "GPL-2.0-only & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://doc/LICENSING.txt;md5=e00dd5c8ac03a14c5ae5225a4525fa2d \
                    "

SRC_URI = "https://github.com/ivmai/libatomic_ops/releases/download/v${PV}/libatomic_ops-${PV}.tar.gz"
UPSTREAM_CHECK_URI = "https://github.com/ivmai/libatomic_ops/releases"

SRC_URI[sha256sum] = "f0ab566e25fce08b560e1feab6a3db01db4a38e5bc687804334ef3920c549f3e"

S = "${WORKDIR}/libatomic_ops-${PV}"

ALLOW_EMPTY:${PN} = "1"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
