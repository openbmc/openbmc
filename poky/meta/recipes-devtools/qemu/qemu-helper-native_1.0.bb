SUMMARY = "Helper utilities needed by the runqemu script"
LICENSE = "GPL-2.0-only"
RDEPENDS:${PN} = "qemu-system-native"

LIC_FILES_CHKSUM = "file://${S}/qemu-oe-bridge-helper.c;endline=4;md5=ae00a3bab86f2caaa8462eacda77f4d7"

SRC_URI = "file://qemu-oe-bridge-helper.c"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit native

do_compile() {
	${CC} ${CFLAGS} ${LDFLAGS} -Wall qemu-oe-bridge-helper.c -o qemu-oe-bridge-helper
}

do_install() {
	install -d ${D}${bindir}
	install qemu-oe-bridge-helper ${D}${bindir}/
}

DEPENDS += "qemu-system-native unfs3-native pseudo-native"
addtask addto_recipe_sysroot after do_populate_sysroot before do_build
