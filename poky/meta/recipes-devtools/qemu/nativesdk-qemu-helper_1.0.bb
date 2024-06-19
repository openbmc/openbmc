SUMMARY = "Qemu helper scripts"
LICENSE = "GPL-2.0-only"
RDEPENDS:${PN} = "nativesdk-qemu nativesdk-unfs3 nativesdk-pseudo \
                  nativesdk-python3-shell nativesdk-python3-fcntl nativesdk-python3-logging \
                "


LIC_FILES_CHKSUM = "file://${COREBASE}/scripts/runqemu;beginline=5;endline=10;md5=ac2b489a58739c7628a2604698db5e7f"


SRC_URI = "file://${COREBASE}/scripts/runqemu \
           file://${COREBASE}/scripts/runqemu-addptable2image \
           file://${COREBASE}/scripts/runqemu-gen-tapdevs \
           file://${COREBASE}/scripts/runqemu-ifup \
           file://${COREBASE}/scripts/runqemu-ifdown \
           file://${COREBASE}/scripts/oe-find-native-sysroot \
           file://${COREBASE}/scripts/runqemu-extract-sdk \
           file://${COREBASE}/scripts/runqemu-export-rootfs \
          "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit nativesdk

do_compile() {
	:
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}${COREBASE}/scripts/oe-* ${D}${bindir}/
	install -m 0755 ${S}${COREBASE}/scripts/runqemu* ${D}${bindir}/
}
