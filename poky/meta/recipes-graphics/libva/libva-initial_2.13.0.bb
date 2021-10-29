require libva.inc

do_install:append () {
	rm -f ${D}${libdir}/*.so*
}
