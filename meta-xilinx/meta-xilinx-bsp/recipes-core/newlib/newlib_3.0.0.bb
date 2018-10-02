require newlib.inc

do_configure_prepend_microblaze() {
	# hack for microblaze, which needs xilinx.ld to literally do any linking (its hard coded in its LINK_SPEC)
	export CC="${CC} -L${S}/libgloss/microblaze"
}

do_configure() {
	${S}/configure ${EXTRA_OECONF}
}

do_install_append() {
	# Move include files and libs to default directories so they can be picked up later
	mv -v ${D}${prefix}/${TARGET_SYS}/lib ${D}${libdir}
	mv -v ${D}${prefix}/${TARGET_SYS}/include ${D}${includedir}

	# Remove original directory
	rmdir ${D}${prefix}/${TARGET_SYS}
}
