require libva.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=2e48940f94acb0af582e5ef03537800f"
SRC_URI[sha256sum] = "a00ff19d9f969259b9784172adad7788dbf3de827d985c5d27c230efd5d98a04"

do_install_append () {
	rm -f ${D}${libdir}/*.so*
}
