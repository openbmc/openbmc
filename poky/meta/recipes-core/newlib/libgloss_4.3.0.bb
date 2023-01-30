require newlib.inc

DEPENDS += "newlib"

FILESEXTRAPATHS:prepend := "${THISDIR}/libgloss:"

SRC_URI:append = " file://libgloss-build-without-nostdinc.patch"
SRC_URI:append:powerpc = " file://fix-rs6000-crt0.patch"
SRC_URI:append:powerpc = " file://fix-rs6000-cflags.patch"

do_configure() {
	${S}/libgloss/configure ${EXTRA_OECONF}
}

do_install:prepend() {
	# install doesn't create this itself, avoid install error
	install -d ${D}${prefix}/${TARGET_SYS}/lib
}

do_install:append() {
	# Move libs to default directories so they can be picked up later
	install -d ${D}${libdir}
	mv -v ${D}${prefix}/${TARGET_SYS}/lib/* ${D}${libdir}

	# Remove original directory
	rmdir -p --ignore-fail-on-non-empty ${D}${prefix}/${TARGET_SYS}/lib
}

# Split packages correctly
FILES:${PN} += "${libdir}/*.ld ${libdir}/*.specs"
FILES:${PN}-dev += "${libdir}/cpu-init/*"
# RiscV installation moved the syscall header to this location
FILES:${PN}-dev += "${prefix}/${TARGET_SYS}/include/machine/*.h"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
