SUMMARY = "Use previously captured traffic to test network devices"

HOMEPAGE = "https://tcpreplay.appneta.com/"

SECTION = "net"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=10f0474a2f0e5dccfca20f69d6598ad8"

SRC_URI = "https://github.com/appneta/${BPN}/releases/download/v${PV}/${BP}.tar.gz \
    file://0001-libopts.m4-set-POSIX_SHELL-to-bin-sh.patch \
"

SRC_URI[sha256sum] = "cc3642816073fb1d96b3af36df4fb66c11f523da427f95b7b0c4c99deaa53afb"

UPSTREAM_CHECK_URI = "https://github.com/appneta/tcpreplay/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v(?P<pver>\d+(\.\d+)+)"

DEPENDS = "libpcap"

EXTRA_OECONF += "--with-libpcap=${STAGING_DIR_HOST}${prefix}"

inherit siteinfo autotools-brokensep

do_install:append() {
    # configure --with-libpcap=${STAGING_DIR_HOST}${prefix} bakes the absolute
    # recipe-sysroot path into defines.h's pcap.h include and into
    # libtcpreplay.pc's Libs.private -L flag, tripping the buildpaths QA
    # check. Strip it from both the source tree copy (shipped verbatim into
    # the tcpreplay-src debug package) and the installed copies under ${D}.
    sed -i -e 's:${RECIPE_SYSROOT}::g' ${S}/src/defines.h
    sed -i -e 's:${RECIPE_SYSROOT}::g' \
        ${D}${includedir}/tcpreplay/defines.h \
        ${D}${libdir}/pkgconfig/libtcpreplay.pc
}
