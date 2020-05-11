SUMMARY = "Generates traces of I/O traffic on block devices"
HOMEPAGE = "http://brick.kernel.dk/snaps/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

DEPENDS = "libaio"

SRCREV = "cca113f2fe0759b91fd6a0e10fdcda2c28f18a7e"

PV = "1.2.0+git${SRCPV}"

SRC_URI = "git://git.kernel.dk/blktrace.git \
           file://ldflags.patch \
           file://CVE-2018-10689.patch \
           file://make-btt-scripts-python3-ready.patch \
"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "\
    'CC=${CC}' \
    'CFLAGS=${CFLAGS}' \
    'LDFLAGS=${LDFLAGS}' \
"

# There are a few parallel issues:
# 1) ../rbtree.o: error adding symbols: Invalid operation
# collect2: error: ld returned 1 exit status
# Makefile:42: recipe for target 'btt' failed
# 2) git/blkiomon.c:216: undefined reference to `rb_insert_color'
# collect2: error: ld returned 1 exit status
# Makefile:27: recipe for target 'blkparse' failed
# 3) ld: rbtree.o: invalid string offset 128 >= 125 for section `.strtab'
# 4) btreplay.o: file not recognized: File truncated
# collect2: error: ld returned 1 exit status
# btreplay/btreplay.c:47:18: fatal error: list.h: No such file or directory
PARALLEL_MAKE = ""

do_install() {
	oe_runmake ARCH="${ARCH}" prefix=${prefix} \
		mandir=${mandir} DESTDIR=${D} install
}

