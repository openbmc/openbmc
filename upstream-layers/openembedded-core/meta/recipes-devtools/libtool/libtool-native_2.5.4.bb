require libtool-${PV}.inc

DEPENDS = ""

inherit native

do_install () {
	autotools_do_install
	install -d ${D}${bindir}/
	install -m 0755 libtool ${D}${bindir}/libtool
}
