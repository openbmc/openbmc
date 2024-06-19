require libtool-${PV}.inc

FILESEXTRAPATHS =. "${FILE_DIRNAME}/libtool:"

SRC_URI += "file://fixinstall.patch"

inherit nativesdk

S = "${WORKDIR}/libtool-${PV}"
FILES:${PN} += "${datadir}/libtool/*"

do_install () {
	autotools_do_install
	install -d ${D}${bindir}/
	install -m 0755 libtool ${D}${bindir}/
}

SYSROOT_PREPROCESS_FUNCS += "libtoolnativesdk_sysroot_preprocess"

libtoolnativesdk_sysroot_preprocess () {
	install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}/
	install -m 755 ${D}${bindir}/libtool ${SYSROOT_DESTDIR}${bindir_crossscripts}/libtool
}
