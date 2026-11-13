SUMMARY = "Use previously captured traffic to test network devices"

HOMEPAGE = "https://tcpreplay.appneta.com/"

SECTION = "net"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "https://github.com/appneta/${BPN}/releases/download/v${PV}/${BP}.tar.gz \
    file://0001-libopts.m4-set-POSIX_SHELL-to-bin-sh.patch \
"

SRC_URI[sha256sum] = "ac4cbc58b4121edb273bc7d4603dfd6a762e7d344981233c8a253425d9dddd8f"

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
