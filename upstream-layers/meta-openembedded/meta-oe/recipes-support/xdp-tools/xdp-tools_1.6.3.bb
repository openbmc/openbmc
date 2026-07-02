SUMMARY = "Utilities and library for managing XDP/eBPF programs"

DESCRIPTION = "xdp-tools provides libxdp and command-line utilities for working \
with XDP eBPF programs in Linux, including tools for loading programs, packet \
processing, monitoring, traffic generation, and benchmarking."

HOMEPAGE = "https://github.com/xdp-project/xdp-tools"
LICENSE = "GPL-2.0-only & GPL-2.0-or-later & LGPL-2.1-only & BSD-2-Clause"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=9ee53f8d06bbdb4c11b1557ecc4f8cd5 \
    file://LICENSES/GPL-2.0;md5=994331978b428511800bfbd17eea3001 \
    file://LICENSES/LGPL-2.1;md5=b370887980db5dd40659b50909238dbd \
    file://LICENSES/BSD-2-Clause;md5=5d6306d1b08f8df623178dfd81880927 \
"
SRC_URI = " \
    git://github.com/xdp-project/xdp-tools.git;tag=v${PV};nobranch=1;protocol=https \
    file://0001-configure-correctly-handle-CC-when-validating-requir.patch \
    file://0002-lib-libxdp-Makefile-use-cp-fRd-to-preserve-symlinks-.patch \
"
SRCREV = "8fbad9f0af621a22aa87ff2520b3735915b1f0fd"

DEPENDS += " \
    libbpf \
    clang-native \
    zlib \
    elfutils \
    libpcap \
"

inherit pkgconfig

EXTRA_OEMAKE += " \
    PREFIX=${prefix} \
    LIBDIR=${libdir} \
    PRODUCTION=1 \
    BPF_CFLAGS='-D__${TARGET_ARCH}__ -I${S}/headers -I${STAGING_INCDIR} -ffile-prefix-map=${S}=${TARGET_DBGSRC_DIR} -ffile-prefix-map=${STAGING_DIR_HOST}=' \
"

do_install () {
    oe_runmake DESTDIR=${D} install
}

# libxdp.so loads BPF objects at runtime from ${libdir}/bpf, not embedded in the .so.
FILES:${PN} += "${libdir}/bpf ${libdir}/bpf/*"

# Test scripts under ${datadir}/xdp-tools require bash.
RDEPENDS:${PN} += "bash"
