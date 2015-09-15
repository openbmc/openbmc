SUMMARY = "Helper utilities needed by the runqemu script"
LICENSE = "GPLv2"
RDEPENDS_${PN} = "qemu-native"
PR = "r1"

LIC_FILES_CHKSUM = "file://${WORKDIR}/tunctl.c;endline=4;md5=ff3a09996bc5fff6bc5d4e0b4c28f999"

SRC_URI = "file://tunctl.c"

S = "${WORKDIR}"

inherit native

do_compile() {
	${CC} tunctl.c -o tunctl
}

do_install() {
	install -d ${D}${bindir}
	install tunctl ${D}${bindir}/
}
