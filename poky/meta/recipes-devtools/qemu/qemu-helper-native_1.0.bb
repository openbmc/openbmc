SUMMARY = "Helper utilities needed by the runqemu script"
LICENSE = "GPL-2.0-only"
RDEPENDS:${PN} = "qemu-system-native"
PR = "r1"

LIC_FILES_CHKSUM = "file://${WORKDIR}/tunctl.c;endline=4;md5=ff3a09996bc5fff6bc5d4e0b4c28f999"

SRC_URI = "\
    file://tunctl.c \
    file://qemu-oe-bridge-helper.c \
    "

S = "${WORKDIR}"

inherit native

do_compile() {
	${CC} ${CFLAGS} ${LDFLAGS} -Wall tunctl.c -o tunctl
	${CC} ${CFLAGS} ${LDFLAGS} -Wall qemu-oe-bridge-helper.c -o qemu-oe-bridge-helper
}

do_install() {
	install -d ${D}${bindir}
	install tunctl ${D}${bindir}/
	install qemu-oe-bridge-helper ${D}${bindir}/
}

DEPENDS += "qemu-system-native"
addtask addto_recipe_sysroot after do_populate_sysroot before do_build
