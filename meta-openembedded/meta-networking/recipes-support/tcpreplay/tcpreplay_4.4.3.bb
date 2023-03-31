SUMMARY = "Use previously captured traffic to test network devices"

HOMEPAGE = "https://tcpreplay.appneta.com/"

SECTION = "net"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=10f0474a2f0e5dccfca20f69d6598ad8"

SRC_URI = "https://github.com/appneta/tcpreplay/releases/download/v${PV}/tcpreplay-${PV}.tar.gz \
           file://0001-libopts.m4-set-POSIX_SHELL-to-bin-sh.patch \
          "

SRC_URI[sha256sum] = "216331692e10c12d7f257945e777928d79bd091117f3e4ffb5b312eb2ca0bf7c"

UPSTREAM_CHECK_URI = "https://github.com/appneta/tcpreplay/releases"

DEPENDS = "libpcap"

EXTRA_OECONF += "--with-libpcap=${STAGING_DIR_HOST}/usr"

inherit siteinfo autotools-brokensep

do_install:append() {
    sed -i -e 's:${RECIPE_SYSROOT}::g' ${S}/src/defines.h
}
