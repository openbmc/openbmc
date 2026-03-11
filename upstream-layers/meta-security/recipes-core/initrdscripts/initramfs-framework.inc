FILESEXTRAPATHS:prepend := "${THISDIR}/initramfs-framework-dm:"

SRC_URI:append = "\
    file://dmverity \
"

do_install:append() {
    # dm-verity
    install ${S}/dmverity ${D}/init.d/80-dmverity
}

PACKAGES:append = " initramfs-module-dmverity"

SUMMARY:initramfs-module-dmverity = "initramfs dm-verity rootfs support"
RDEPENDS:initramfs-module-dmverity = "${PN}-base"
FILES:initramfs-module-dmverity = "/init.d/80-dmverity"
