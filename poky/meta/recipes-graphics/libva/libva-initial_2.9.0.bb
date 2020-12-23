require libva.inc

do_install_append () {
	rm -f ${D}${libdir}/*.so*
}
