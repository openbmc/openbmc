SUMMARY = "Use previously captured traffic to test network devices"

HOMEPAGE = "https://tcpreplay.appneta.com/"

SECTION = "net"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=10f0474a2f0e5dccfca20f69d6598ad8"

SRC_URI = "https://github.com/appneta/tcpreplay/releases/download/v${PV}/tcpreplay-${PV}.tar.gz \
           file://0001-libopts.m4-set-POSIX_SHELL-to-bin-sh.patch \
          "

SRC_URI[sha256sum] = "5b272cd83b67d6288a234ea15f89ecd93b4fadda65eddc44e7b5fcb2f395b615"

UPSTREAM_CHECK_URI = "https://github.com/appneta/tcpreplay/releases"

DEPENDS = "libpcap"

EXTRA_OECONF += "--with-libpcap=${STAGING_DIR_HOST}/usr"

inherit siteinfo autotools-brokensep

do_install:append() {
    sed -i -e 's:${RECIPE_SYSROOT}::g' ${S}/src/defines.h
}
