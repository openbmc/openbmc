SUMMARY = "Modular initramfs system"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
RDEPENDS_${PN} += "${VIRTUAL-RUNTIME_base-utils}"

PR = "r2"

inherit allarch

SRC_URI = "file://init \
           file://finish \
           file://mdev \
           file://udev \
           file://e2fs \
           file://debug"

S = "${WORKDIR}"

do_install() {
    install -d ${D}/init.d

    # base
    install -m 0755 ${WORKDIR}/init ${D}/init
    install -m 0755 ${WORKDIR}/finish ${D}/init.d/99-finish

    # mdev
    install -m 0755 ${WORKDIR}/mdev ${D}/init.d/01-mdev

    # udev
    install -m 0755 ${WORKDIR}/udev ${D}/init.d/01-udev

    # e2fs
    install -m 0755 ${WORKDIR}/e2fs ${D}/init.d/10-e2fs

    # debug
    install -m 0755 ${WORKDIR}/debug ${D}/init.d/00-debug

    # Create device nodes expected by some kernels in initramfs
    # before even executing /init.
    install -d ${D}/dev
    mknod -m 622 ${D}/dev/console c 5 1
}

PACKAGES = "${PN}-base \
            initramfs-module-mdev \
            initramfs-module-udev \
            initramfs-module-e2fs \
            initramfs-module-debug"

FILES_${PN}-base = "/init /init.d/99-finish /dev"

SUMMARY_initramfs-module-mdev = "initramfs support for mdev"
RDEPENDS_initramfs-module-mdev = "${PN}-base"
FILES_initramfs-module-mdev = "/init.d/01-mdev"

SUMMARY_initramfs-module-udev = "initramfs support for udev"
RDEPENDS_initramfs-module-udev = "${PN}-base udev"
FILES_initramfs-module-udev = "/init.d/01-udev"

SUMMARY_initramfs-module-e2fs = "initramfs support for ext4/ext3/ext2 filesystems"
RDEPENDS_initramfs-module-e2fs = "${PN}-base"
FILES_initramfs-module-e2fs = "/init.d/10-e2fs"

SUMMARY_initramfs-module-debug = "initramfs dynamic debug support"
RDEPENDS_initramfs-module-debug = "${PN}-base"
FILES_initramfs-module-debug = "/init.d/00-debug"
