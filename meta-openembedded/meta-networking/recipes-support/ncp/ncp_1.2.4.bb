SUMMARY = "a fast file copy tool for LANs"
DESCRIPTION = "ncp is a utility for copying files in a LAN. It has absolutely no \
security or integrity checking, no throttling, no features, except \
one: you don't have to type the coordinates of your peer."
HOMEPAGE = "http://www.fefe.de/ncp"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"
DEPENDS = "libowfat"

SRC_URI = "https://dl.fefe.de/${BP}.tar.bz2"
SRC_URI[md5sum] = "421c4855bd3148b7d0a4342942b4bf13"
SRC_URI[sha256sum] = "6cfa72edd5f7717bf7a4a93ccc74c4abd89892360e2e0bb095a73c24b9359b88"

EXTRA_OEMAKE = "\
    DIET= \
    DEBUG=nostrip \
    CC='${CC}' CFLAGS='${CFLAGS} -I${STAGING_INCDIR}/libowfat' LDFLAGS='${LDFLAGS}' \
"

do_install() {
    install -d -m0755 ${D}${bindir} ${D}${mandir}/man1

    install -m0755 ncp ${D}${bindir}
    ln -sf ncp ${D}${bindir}/npoll
    ln -sf ncp ${D}${bindir}/npush

    install -m0644 ncp.1 npush.1 ${D}${mandir}/man1
    ln -sf npush.1 ${D}${mandir}/man1/npoll.1
}

BBCLASSEXTEND = "native nativesdk"
