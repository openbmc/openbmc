SUMMARY = "Utility to test for faulty memory subsystem"
HOMEPAGE = "http://pyropus.ca/software/memtester/"
SECTION = "console/utils"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "http://pyropus.ca/software/memtester/old-versions/${BP}.tar.gz \
           file://Makefile.patch \
           "
SRC_URI[md5sum] = "674a8a88ff54bdb229ca4148218a41f1"
SRC_URI[sha256sum] = "8ed52b0d06d4aeb61954994146e2a5b2d20448a8f3ce3ee995120e6dbde2ae37"

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
