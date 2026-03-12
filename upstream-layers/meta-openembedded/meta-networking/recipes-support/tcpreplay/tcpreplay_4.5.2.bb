SUMMARY = "Use previously captured traffic to test network devices"

HOMEPAGE = "https://tcpreplay.appneta.com/"

SECTION = "net"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=10f0474a2f0e5dccfca20f69d6598ad8"

SRC_URI = "https://github.com/appneta/${BPN}/releases/download/v${PV}/${BP}.tar.gz \
    file://0001-libopts.m4-set-POSIX_SHELL-to-bin-sh.patch \
"

SRC_URI[sha256sum] = "ccff3bb29469a04ccc20ed0b518e3e43c4a7b5a876339d9435bfd9db7fe5d0f1"

UPSTREAM_CHECK_URI = "https://github.com/appneta/tcpreplay/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v(?P<pver>\d+(\.\d+)+)"

DEPENDS = "libpcap"

EXTRA_OECONF += "--with-libpcap=${STAGING_DIR_HOST}${prefix}"

inherit siteinfo autotools-brokensep

do_install:append() {
    sed -i -e 's:${RECIPE_SYSROOT}::g' ${S}/src/defines.h
}
