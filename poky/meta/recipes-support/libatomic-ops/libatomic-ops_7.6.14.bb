SUMMARY = "A library for atomic integer operations"
DESCRIPTION = "Package provides semi-portable access to hardware-provided atomic memory update operations on a number of architectures."
HOMEPAGE = "https://github.com/ivmai/libatomic_ops/"
SECTION = "optional"
PROVIDES += "libatomics-ops"
LICENSE = "GPL-2.0-only & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://doc/LICENSING.txt;md5=dfc50c7cea7b66935844587a0f7389e7 \
                    "

SRC_URI = "https://github.com/ivmai/libatomic_ops/releases/download/v${PV}/libatomic_ops-${PV}.tar.gz"
UPSTREAM_CHECK_URI = "https://github.com/ivmai/libatomic_ops/releases"

SRC_URI[sha256sum] = "390f244d424714735b7050d056567615b3b8f29008a663c262fb548f1802d292"

S = "${WORKDIR}/libatomic_ops-${PV}"

ALLOW_EMPTY:${PN} = "1"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
