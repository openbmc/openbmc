SUMMARY = "Utility to test for faulty memory subsystem"
HOMEPAGE = "http://pyropus.ca/software/memtester/"
SECTION = "console/utils"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "http://pyropus.ca/software/memtester/old-versions/${BP}.tar.gz \
           file://Makefile.patch \
           "
SRC_URI[md5sum] = "1bc22c01e987d6a67fac39dc5656a4d3"
SRC_URI[sha256sum] = "1c5fc2382576c084b314cfd334d127a66c20bd63892cac9f445bc1d8b4ca5a47"

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
