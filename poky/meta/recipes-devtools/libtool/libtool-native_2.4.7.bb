require libtool-${PV}.inc

DEPENDS = ""

inherit native

EXTRA_OECONF = " --with-libtool-sysroot=${STAGING_DIR_NATIVE}"

do_configure:prepend () {
	# Remove any existing libtool m4 since old stale versions would break
	# any upgrade
	rm -f ${STAGING_DATADIR}/aclocal/libtool.m4
	rm -f ${STAGING_DATADIR}/aclocal/lt*.m4
}

do_install () {
	autotools_do_install
	install -d ${D}${bindir}/
	install -m 0755 libtool ${D}${bindir}/libtool
}
