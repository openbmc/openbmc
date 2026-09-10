require libva.inc

PACKAGECONFIG ?= ""

do_install:append () {
	rm -f ${D}${libdir}/*.so*
}

