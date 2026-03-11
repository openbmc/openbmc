require newlib.inc

PROVIDES += "virtual/libc virtual/libiconv virtual/libintl"

do_configure() {
    export CC_FOR_TARGET="${CC}"
    ${S}/configure ${EXTRA_OECONF}
}

do_install:append() {
	# Move include files and libs to default directories so they can be picked up later
	mv -v ${D}${prefix}/${TARGET_SYS}/lib ${D}${libdir}
	mv -v ${D}${prefix}/${TARGET_SYS}/include ${D}${includedir}

	# Remove original directory
	rmdir ${D}${prefix}/${TARGET_SYS}
}

# No rpm package is actually created but -dev depends on it, avoid dnf error
DEV_PKG_DEPENDENCY:libc-newlib = ""
