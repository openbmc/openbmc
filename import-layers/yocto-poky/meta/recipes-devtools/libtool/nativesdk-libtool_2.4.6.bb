require libtool-${PV}.inc

FILESEXTRAPATHS =. "${FILE_DIRNAME}/libtool:"

SRC_URI += "file://prefix.patch"
SRC_URI += "file://fixinstall.patch"

inherit nativesdk

S = "${WORKDIR}/libtool-${PV}"
FILES_${PN} += "${datadir}/libtool/*"

do_configure_prepend () {
	# Remove any existing libtool m4 since old stale versions would break
	# any upgrade
	rm -f ${STAGING_DATADIR}/aclocal/libtool.m4
	rm -f ${STAGING_DATADIR}/aclocal/lt*.m4
}

do_install () {
	autotools_do_install
	install -d ${D}${bindir}/
	install -m 0755 ${HOST_SYS}-libtool ${D}${bindir}/
}

SYSROOT_PREPROCESS_FUNCS += "libtoolnativesdk_sysroot_preprocess"

libtoolnativesdk_sysroot_preprocess () {
	install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}/
	install -m 755 ${D}${bindir}/${HOST_SYS}-libtool ${SYSROOT_DESTDIR}${bindir_crossscripts}/${HOST_SYS}-libtool
}
