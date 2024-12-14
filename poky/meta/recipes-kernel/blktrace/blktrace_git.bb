SUMMARY = "Generates traces of I/O traffic on block devices"
DESCRIPTION = "blktrace is a block layer IO tracing mechanism which provides \
detailed information about request queue operations up to user space. There \
are three major components: a kernel component, a utility to record the i/o \
trace information for the kernel to user space, and utilities to analyse and \
view the trace information."
HOMEPAGE = "http://brick.kernel.dk/snaps/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

DEPENDS = "libaio"

SRCREV = "366d30b9cdb20345c5d064af850d686da79b89eb"

PV = "1.3.0+git"

SRC_URI = "git://git.kernel.dk/blktrace.git;branch=master;protocol=https \
           file://0001-bno_plot.py-btt_plot.py-Ask-for-python3-specifically.patch \
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

