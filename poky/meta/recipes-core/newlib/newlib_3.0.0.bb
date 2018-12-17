require newlib.inc

PROVIDES += "virtual/libc virtual/${TARGET_PREFIX}libc-for-gcc virtual/libiconv virtual/libintl"

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

# No rpm package is actually created but -dev depends on it, avoid dnf error
RDEPENDS_${PN}-dev_libc-newlib = ""
