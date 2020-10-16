SUMMARY = "Utility to test for faulty memory subsystem"
HOMEPAGE = "http://pyropus.ca/software/memtester/"
SECTION = "console/utils"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "http://pyropus.ca/software/memtester/old-versions/${BP}.tar.gz \
           file://Makefile.patch \
           "
SRC_URI[md5sum] = "e1883b69cd7c0bb74ef6a475c93a4fbf"
SRC_URI[sha256sum] = "6ffe23e6e6449b42c577c7953778fb6f698050196797a94fc619d9badc59f8e8"

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
