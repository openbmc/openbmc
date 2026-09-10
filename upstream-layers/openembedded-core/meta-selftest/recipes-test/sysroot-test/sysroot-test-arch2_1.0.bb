LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PROVIDES = "virtual/sysroot-test"
INHIBIT_DEFAULT_DEPS = "1"
PACKAGE_ARCH = "${MACHINE_ARCH}"

TESTSTRING ?= "2"

do_install() {
	install -d ${D}${includedir}
	echo "# test ${TESTSTRING}" > ${D}${includedir}/sysroot-test.h
}

EXCLUDE_FROM_WORLD = "1"
