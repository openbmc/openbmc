LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

LICENSE = "MIT"

SRC_URI = "file://hello.c \
           file://gdb.sh \
"

S = "${WORKDIR}"

do_compile () {
	${CC} hello.c -o hello1 ${CFLAGS} ${LDFLAGS}

	${CC} hello.c -c -o hello.o ${CFLAGS}
	${AR} rcs libhello.a hello.o
}

do_install () {
	install -d ${D}${bindir}
	install -m 755 ${WORKDIR}/gdb.sh ${D}${bindir}/
	install -m 755 hello1 ${D}${bindir}/hello1
	ln ${D}${bindir}/hello1 ${D}${bindir}/hello2

	install -d ${D}${libexecdir}
	ln ${D}${bindir}/hello1 ${D}${libexecdir}/hello3
	ln ${D}${bindir}/hello1 ${D}${libexecdir}/hello4

	# We need so many hardlink copies to look for specific race conditions
	install -d ${D}${libdir}
	install -m 0644 libhello.a ${D}${libdir}
	for num in `seq 1 100` ; do
		ln ${D}${libdir}/libhello.a ${D}${libdir}/libhello-${num}.a
	done

	dd if=/dev/zero of=${D}${bindir}/sparsetest bs=1 count=0 seek=1M
}

RDEPENDS:${PN}-gdb += "gdb"
PACKAGES =+ "${PN}-gdb"
FILES:${PN}-gdb = "${bindir}/gdb.sh"

PACKAGE_STRIP_STATIC = "1"
PACKAGE_DEBUG_STATIC_SPLIT = "1"
