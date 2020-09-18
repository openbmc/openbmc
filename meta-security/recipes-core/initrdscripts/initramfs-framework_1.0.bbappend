FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = "\
    file://dmverity \
"

do_install_append() {
    # dm-verity
    install ${WORKDIR}/dmverity ${D}/init.d/80-dmverity
}

PACKAGES_append = " initramfs-module-dmverity"

SUMMARY_initramfs-module-dmverity = "initramfs dm-verity rootfs support"
RDEPENDS_initramfs-module-dmverity = "${PN}-base"
FILES_initramfs-module-dmverity = "/init.d/80-dmverity"
