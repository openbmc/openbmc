SUMMARY = "Utility to test for faulty memory subsystem"
HOMEPAGE = "http://pyropus.ca/software/memtester/"
SECTION = "console/utils"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "http://pyropus.ca/software/memtester/old-versions/${BP}.tar.gz \
           file://Makefile.patch \
           "
SRC_URI[sha256sum] = "c9fe4eb7e80c8cef5202f9065c4c0682f5616647c0455e916a5700f98e3dbb2e"

do_compile () {
    echo '${CC} ${CFLAGS} -DPOSIX -c' > conf-cc
    echo '${CC} ${LDFLAGS}' > conf-ld
    oe_runmake
}

do_install () {
    install -d ${D}${bindir}
    install -d ${D}${mandir}/man8
    install -m 0755 memtester ${D}${bindir}/
    install -m 0755 memtester.8 ${D}${mandir}/man8/
}
