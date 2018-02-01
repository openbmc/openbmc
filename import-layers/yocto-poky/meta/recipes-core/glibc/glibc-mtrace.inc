require glibc-collateral.inc

SUMMARY = "mtrace utility provided by glibc"
DESCRIPTION = "mtrace utility provided by glibc"
RDEPENDS_${PN} = "perl"
RPROVIDES_${PN} = "libc-mtrace"

SRC = "${COMPONENTS_DIR}/${PACKAGE_ARCH}/glibc-stash-locale/scripts"

do_install() {
	install -d -m 0755 ${D}${bindir}
	install -m 0755 ${SRC}/mtrace ${D}${bindir}/
}
