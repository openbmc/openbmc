SRC_URI += "file://99-mtd-fw-env.rules"

do_install_append() {
	install -d ${D}/${base_libdir}/udev/rules.d/
        install ${WORKDIR}/99-mtd-fw-env.rules ${D}/${base_libdir}/udev/rules.d/
}
