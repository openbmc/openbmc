SUMMARY = "Helper utilities needed by the runqemu script"
LICENSE = "GPLv2"
RDEPENDS_${PN} = "qemu-system-native"
PR = "r1"

LIC_FILES_CHKSUM = "file://${WORKDIR}/tunctl.c;endline=4;md5=ff3a09996bc5fff6bc5d4e0b4c28f999"

SRC_URI = "\
    file://tunctl.c \
    file://qemu-oe-bridge-helper \
    "

S = "${WORKDIR}"

inherit native

do_compile() {
	${CC} ${CFLAGS} ${LDFLAGS} -Wall tunctl.c -o tunctl
}

do_install() {
	install -d ${D}${bindir}
	install tunctl ${D}${bindir}/

    install -m 755 ${WORKDIR}/qemu-oe-bridge-helper ${D}${bindir}/
}

DEPENDS += "qemu-system-native"
addtask addto_recipe_sysroot after do_populate_sysroot before do_build
