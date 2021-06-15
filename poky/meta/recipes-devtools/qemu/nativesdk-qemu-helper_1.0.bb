SUMMARY = "Qemu helper scripts"
LICENSE = "GPLv2"
RDEPENDS_${PN} = "nativesdk-qemu \
                  nativesdk-python3-shell nativesdk-python3-fcntl nativesdk-python3-logging \
                "

PR = "r9"

LIC_FILES_CHKSUM = "file://${WORKDIR}/tunctl.c;endline=4;md5=ff3a09996bc5fff6bc5d4e0b4c28f999 \
                    file://${COREBASE}/scripts/runqemu;beginline=5;endline=10;md5=ac2b489a58739c7628a2604698db5e7f"


SRC_URI = "file://${COREBASE}/scripts/runqemu \
           file://${COREBASE}/scripts/runqemu-addptable2image \
           file://${COREBASE}/scripts/runqemu-gen-tapdevs \
           file://${COREBASE}/scripts/runqemu-ifup \
           file://${COREBASE}/scripts/runqemu-ifdown \
           file://${COREBASE}/scripts/oe-find-native-sysroot \
           file://${COREBASE}/scripts/runqemu-extract-sdk \
           file://${COREBASE}/scripts/runqemu-export-rootfs \
           file://tunctl.c \
          "

S = "${WORKDIR}"

inherit nativesdk

do_compile() {
	${CC} tunctl.c -o tunctl
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}${COREBASE}/scripts/oe-* ${D}${bindir}/
	install -m 0755 ${WORKDIR}${COREBASE}/scripts/runqemu* ${D}${bindir}/
	install tunctl ${D}${bindir}/
}
