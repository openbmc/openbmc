DESCRIPTION = "rootkit detector"
SUMMARY = "locally checks for signs of a rootkit"
HOMEPAGE = "http://www.chkrootkit.org/"
SECTION = "security"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=6db4d77fb8f0cc84d175e7a1211e4c13"

SRC_URI = "http://archive.ubuntu.com/ubuntu/pool/universe/c/${BPN}/${BPN}_${PV}.orig.tar.gz \
           file://musl_fix.patch"

SRC_URI[sha256sum] = "75ed2ace81f0fa3e9c3fb64dab0e8857ed59247ea755f5898416feb2c66807b9"

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
