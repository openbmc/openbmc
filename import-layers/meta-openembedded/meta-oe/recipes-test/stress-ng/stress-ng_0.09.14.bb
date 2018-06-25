SUMMARY = "A tool to load and stress a computer system"
HOMEPAGE = "http://kernel.ubuntu.com/~cking/stress-ng/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "zlib libaio"

SRC_URI = "http://kernel.ubuntu.com/~cking/tarballs/${BPN}/${BP}.tar.xz \
           file://0002-stress-fcntl-fix-build-for-musl.patch \
          "
SRC_URI_append_libc-musl = " \
    file://0001-Several-changes-to-fix-musl-build.patch \
    "
SRC_URI[md5sum] = "1f8b6c2c2830704d2a2814c16082d48e"
SRC_URI[sha256sum] = "02cac34a5cb041197af60c1867844c6cbb089a6d10a38cdcf7b8f27bfaa6ef8f"

UPSTREAM_CHECK_URI ?= "http://kernel.ubuntu.com/~cking/tarballs/${BPN}/"
UPSTREAM_CHECK_REGEX ?= "(?P<pver>\d+(\.\d+)+)\.tar"

CFLAGS += "-Wall -Wextra -DVERSION='"$(VERSION)"'"

do_install_append() {
    install -d ${D}${bindir}
    install -m 755 ${S}/stress-ng ${D}${bindir}/stress-ng
}
