require glibc-collateral.inc

SUMMARY = "utility scripts provided by glibc"
DESCRIPTION = "utility scripts provided by glibc"
RDEPENDS_${PN} = "bash glibc-mtrace"

SRC = "${COMPONENTS_DIR}/${PACKAGE_ARCH}/glibc-stash-locale/scripts"

bashscripts = "sotruss xtrace"

do_install() {
	install -d -m 0755 ${D}${bindir}
	for i in ${bashscripts}; do
		install -m 0755 ${SRC}/$i ${D}${bindir}/
	done
}

# sotruss script requires sotruss-lib.so (given by libsotruss package), 
# to produce trace of the library calls.
RDEPENDS_${PN} += "libsotruss"
