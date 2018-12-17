SUMMARY = "A tool to load and stress a computer system"
HOMEPAGE = "http://kernel.ubuntu.com/~cking/stress-ng/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "zlib libaio"

SRC_URI = "http://kernel.ubuntu.com/~cking/tarballs/${BPN}/${BP}.tar.xz \
           file://0001-Revert-Makefile-force-sync-after-build-in-case-reboo.patch \
           "
SRC_URI_append_libc-musl = " \
    file://0001-Several-changes-to-fix-musl-build.patch \
    "

SRC_URI[md5sum] = "b03744c2eb68bf7e9a300e78e397f348"
SRC_URI[sha256sum] = "0741e3004bf590bb7af3db979a46fe89bee7aaad6065cd1d87d0b7fa49046cb2"

UPSTREAM_CHECK_URI ?= "http://kernel.ubuntu.com/~cking/tarballs/${BPN}/"
UPSTREAM_CHECK_REGEX ?= "(?P<pver>\d+(\.\d+)+)\.tar"

CFLAGS += "-Wall -Wextra -DVERSION='"$(VERSION)"'"

do_install_append() {
    install -d ${D}${bindir}
    install -m 755 ${S}/stress-ng ${D}${bindir}/stress-ng
}
