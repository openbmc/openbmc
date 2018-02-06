FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI_append = " file://76-mtd-partitions.rules"

do_install_append() {
	install -d ${D}/${base_libdir}/udev/rules.d/
	install ${WORKDIR}/76-mtd-partitions.rules ${D}/${base_libdir}/udev/rules.d/
}
