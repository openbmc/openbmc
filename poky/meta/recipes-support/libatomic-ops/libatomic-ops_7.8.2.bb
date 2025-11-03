SUMMARY = "A library for atomic integer operations"
DESCRIPTION = "Package provides semi-portable access to hardware-provided atomic memory update operations on a number of architectures."
HOMEPAGE = "https://github.com/bdwgc/libatomic_ops/"
SECTION = "optional"
PROVIDES += "libatomics-ops"
LICENSE = "GPL-2.0-only & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LICENSE;md5=5700d28353dfa2f191ca9b1bd707865e \
                    "

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/libatomic_ops-${PV}.tar.gz"
GITHUB_BASE_URI = "https://github.com/bdwgc/libatomic_ops/releases"

SRC_URI[sha256sum] = "d305207fe207f2b3fb5cb4c019da12b44ce3fcbc593dfd5080d867b1a2419b51"

S = "${WORKDIR}/libatomic_ops-${PV}"

ALLOW_EMPTY:${PN} = "1"

inherit autotools pkgconfig github-releases

BBCLASSEXTEND = "native nativesdk"
