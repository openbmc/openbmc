
require newlib.inc

DEPENDS += "newlib"

do_configure() {
	${S}/libgloss/configure ${EXTRA_OECONF}
}

do_install_prepend() {
	# install doesn't create this itself, avoid install error
	install -d ${D}${prefix}/${TARGET_SYS}/lib
}

do_install_append() {
	# Move libs to default directories so they can be picked up later
	mv -v ${D}${prefix}/${TARGET_SYS}/lib ${D}${libdir}

	# Remove original directory
	rmdir ${D}${prefix}/${TARGET_SYS}
}

# Split packages correctly
FILES_${PN} += "${libdir}/*.ld ${libdir}/*.specs"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
