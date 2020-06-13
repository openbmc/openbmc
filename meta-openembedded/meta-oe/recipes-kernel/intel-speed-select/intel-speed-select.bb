SUMMARY = "A tool to validate Intel Speed Select commands"

DESCRIPTION = "The Intel Speed Select Technology (Intel SST) is a powerful new \
collection of features giving you more granular control over CPU performance \
for optimized total cost of ownership."

LICENSE = "GPLv2"

inherit kernelsrc

COMPATIBLE_HOST = '(x86_64|i.86).*-linux'
COMPATIBLE_HOST_libc-musl = 'null'

do_populate_lic[depends] += "virtual/kernel:do_patch"

B = "${WORKDIR}/${BPN}-${PV}"

EXTRA_OEMAKE = "-C ${S}/tools/power/x86/intel-speed-select O=${B} CROSS=${TARGET_PREFIX} CC="${CC}" LD="${LD}" AR=${AR} ARCH=${ARCH}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake DESTDIR=${D} install
}
