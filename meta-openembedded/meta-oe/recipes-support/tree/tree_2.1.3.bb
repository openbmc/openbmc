SUMMARY = "A recursive directory listing command"
HOMEPAGE = "https://oldmanprogrammer.net/source.php?dir=projects/tree"
SECTION = "console/utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=393a5ca445f6965873eca0259a17f833"

SRC_URI = "https://oldmanprogrammer.net/tar/tree/${BP}.tgz"
SRC_URI[sha256sum] = "c24c9bfbbb3bd31a43b16898f74fd338cfcf348e914de81438ddfcf052076ff4"

# tree's default CFLAGS for Linux
CFLAGS += "-Wall -DLINUX -D_LARGEFILE64_SOURCE -D_FILE_OFFSET_BITS=64"

EXTRA_OEMAKE = "CC='${CC}' CFLAGS='${CFLAGS}' LDFLAGS='${LDFLAGS}'"

do_configure[noexec] = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/${BPN} ${D}${bindir}/
}
