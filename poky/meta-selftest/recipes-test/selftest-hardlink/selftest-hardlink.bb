LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

LICENSE = "MIT"

SRC_URI = "file://hello.c \
           file://gdb.sh \
"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_compile () {
	${CC} hello.c -o hello1 ${CFLAGS} ${LDFLAGS}
}

do_install () {
	install -d ${D}${bindir}
	install -m 755 ${S}/gdb.sh ${D}${bindir}/
	install -m 755 hello1 ${D}${bindir}/hello1
	ln ${D}${bindir}/hello1 ${D}${bindir}/hello2

	install -d ${D}${libexecdir}
	ln ${D}${bindir}/hello1 ${D}${libexecdir}/hello3
	ln ${D}${bindir}/hello1 ${D}${libexecdir}/hello4

	dd if=/dev/zero of=${D}${bindir}/sparsetest bs=1 count=0 seek=1M
}

RDEPENDS:${PN}-gdb += "gdb"
PACKAGES =+ "${PN}-gdb"
FILES:${PN}-gdb = "${bindir}/gdb.sh"
