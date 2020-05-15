require libva.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=2e48940f94acb0af582e5ef03537800f"
SRC_URI[md5sum] = "aef13eb48e01a47d1416d97462a22a11"
SRC_URI[sha256sum] = "6c57eb642d828af2411aa38f55dc10111e8c98976dbab8fd62e48629401eaea5"

do_install_append () {
	rm -f ${D}${libdir}/*.so*
}
