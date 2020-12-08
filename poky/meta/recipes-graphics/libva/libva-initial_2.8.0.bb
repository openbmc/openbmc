require libva.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=2e48940f94acb0af582e5ef03537800f"
SRC_URI[sha256sum] = "adbb1244d278908f89ccfcf254a442de6d71934565a492cb6f03caf2ed4d1ec3"

do_install_append () {
	rm -f ${D}${libdir}/*.so*
}
