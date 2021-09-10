require glibc-collateral.inc

SUMMARY = "mtrace utility provided by glibc"
DESCRIPTION = "mtrace utility provided by glibc"
RDEPENDS:${PN} = "perl"
RPROVIDES:${PN} = "libc-mtrace"

SRC = "${COMPONENTS_DIR}/${PACKAGE_ARCH}/glibc-stash-locale/scripts"

do_install() {
	install -d -m 0755 ${D}${bindir}
	install -m 0755 ${SRC}/mtrace ${D}${bindir}/
}

# Don't scan for CVEs as glibc will be scanned
CVE_PRODUCT = ""
