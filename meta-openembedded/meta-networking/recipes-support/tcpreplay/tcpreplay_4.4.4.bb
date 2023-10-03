SUMMARY = "Use previously captured traffic to test network devices"

HOMEPAGE = "https://tcpreplay.appneta.com/"

SECTION = "net"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=10f0474a2f0e5dccfca20f69d6598ad8"

SRC_URI = "https://github.com/appneta/${BPN}/releases/download/v${PV}/${BP}.tar.gz \
    file://0001-libopts.m4-set-POSIX_SHELL-to-bin-sh.patch \
    file://0001-configure.ac-unify-search-dirs-for-pcap-and-add-lib3.patch \
"

SRC_URI[sha256sum] = "44f18fb6d3470ecaf77a51b901a119dae16da5be4d4140ffbb2785e37ad6d4bf"

UPSTREAM_CHECK_URI = "https://github.com/appneta/tcpreplay/releases"

DEPENDS = "libpcap"

EXTRA_OECONF += "--with-libpcap=${STAGING_DIR_HOST}${prefix}"

inherit siteinfo autotools-brokensep

do_install:append() {
    sed -i -e 's:${RECIPE_SYSROOT}::g' ${S}/src/defines.h
}
