include glibc-collateral.inc

SUMMARY = "utility scripts provided by glibc"
DESCRIPTION = "utility scripts provided by glibc"
RDEPENDS_${PN} = "bash glibc-mtrace"

SRC = "${STAGING_INCDIR}/glibc-scripts-internal-${MULTIMACH_TARGET_SYS}"

bashscripts = "sotruss xtrace"

do_install() {
	install -d -m 0755 ${D}${bindir}
	for i in ${bashscripts}; do
		install -m 0755 ${SRC}/$i ${D}${bindir}/
	done
}
