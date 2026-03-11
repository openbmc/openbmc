LICENSE = "CLOSED"

PROVIDES = "virtual/sysroot-test"
INHIBIT_DEFAULT_DEPS = "1"
PACKAGE_ARCH = "${MACHINE_ARCH}"

TESTSTRING ?= "2"

do_install() {
	install -d ${D}${includedir}
	echo "# test ${TESTSTRING}" > ${D}${includedir}/sysroot-test.h
}

EXCLUDE_FROM_WORLD = "1"
