DESCRIPTION = "rootkit detector"
SUMMARY = "locally checks for signs of a rootkit"
HOMEPAGE = "http://www.chkrootkit.org/"
SECTION = "security"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=fdbe53788f7081c63387d8087273f5ff"

SRC_URI = "ftp://ftp.pangeia.com.br/pub/seg/pac/${BPN}.tar.gz"
SRC_URI[sha256sum] = "7262dae33b338976828b5d156b70d159e0043c0db43ada8dee66c97387cf45b5"


inherit autotools-brokensep

TARGET_CC_ARCH += "${LDFLAGS}"

do_configure () {
    sed -i 's/@strip.*$//' ${S}/Makefile
}

do_compile () {
    make CC="${CC}" LDFLAGS="${LDFLAGS}" sense
    gzip -9vkf ACKNOWLEDGMENTS
    gzip -9vkf README
}

do_install () {
    install -d ${D}/${libdir}/${PN}
    install -d ${D}/${sbindir}
    install -d ${D}/${docdir}/${PN}

    install -m 644 ${B}/chkdirs ${D}/${libdir}/${PN}
    install -m 644 ${B}/chklastlog ${D}/${libdir}/${PN}
    install -m 644 ${B}/chkproc ${D}/${libdir}/${PN}
    install -m 644 ${B}/chkutmp ${D}/${libdir}/${PN}
    install -m 644 ${B}/chkwtmp ${D}/${libdir}/${PN}
    install -m 644 ${B}/ifpromisc ${D}/${libdir}/${PN}
    install -m 644 ${B}/strings-static ${D}/${libdir}/${PN}

    install -m 755 ${B}/chklastlog ${D}/${sbindir}
    install -m 755 ${B}/chkrootkit ${D}/${sbindir}
    install -m 755 ${B}/chkwtmp ${D}/${sbindir}

    install -m 644 ${B}/ACKNOWLEDGMENTS.gz ${D}/${docdir}/${PN}
    install -m 644 ${B}/README.chklastlog ${D}/${docdir}/${PN}
    install -m 644 ${B}/README.chkwtmp ${D}/${docdir}/${PN}
    install -m 644 ${B}/README.gz ${D}/${docdir}/${PN}
    install -m 644 ${B}/COPYRIGHT ${D}/${docdir}/${PN}
}
