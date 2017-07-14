SRC_URI += "file://99-mtd.rules"

do_install_append() {
	install -d ${D}/${base_libdir}/udev/rules.d/
        install ${WORKDIR}/99-mtd.rules ${D}/${base_libdir}/udev/rules.d/
}
