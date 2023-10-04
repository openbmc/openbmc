SUMMARY = "Modular initramfs system"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
RDEPENDS:${PN} += "${VIRTUAL-RUNTIME_base-utils}"
RRECOMMENDS:${PN} = "${VIRTUAL-RUNTIME_base-utils-syslog}"


inherit allarch

SRC_URI = "file://init \
           file://exec \
           file://nfsrootfs \
           file://rootfs \
           file://finish \
           file://mdev \
           file://udev \
           file://e2fs \
           file://debug \
           file://lvm \
           file://overlayroot \
          "

S = "${WORKDIR}"

do_install() {
    install -d ${D}/init.d

    # base
    install -m 0755 ${WORKDIR}/init ${D}/init
    install -m 0755 ${WORKDIR}/nfsrootfs ${D}/init.d/85-nfsrootfs
    install -m 0755 ${WORKDIR}/rootfs ${D}/init.d/90-rootfs
    install -m 0755 ${WORKDIR}/finish ${D}/init.d/99-finish

    # exec
    install -m 0755 ${WORKDIR}/exec ${D}/init.d/89-exec

    # mdev
    install -m 0755 ${WORKDIR}/mdev ${D}/init.d/01-mdev

    # udev
    install -m 0755 ${WORKDIR}/udev ${D}/init.d/01-udev

    # e2fs
    install -m 0755 ${WORKDIR}/e2fs ${D}/init.d/10-e2fs

    # debug
    install -m 0755 ${WORKDIR}/debug ${D}/init.d/00-debug

    # lvm
    install -m 0755 ${WORKDIR}/lvm ${D}/init.d/09-lvm

    # overlayroot needs to run after rootfs module but before finish
    install -m 0755 ${WORKDIR}/overlayroot ${D}/init.d/91-overlayroot

    # Create device nodes expected by some kernels in initramfs
    # before even executing /init.
    install -d ${D}/dev
    mknod -m 622 ${D}/dev/console c 5 1
}

PACKAGES = "${PN}-base \
            initramfs-module-exec \
            initramfs-module-mdev \
            initramfs-module-udev \
            initramfs-module-e2fs \
            initramfs-module-nfsrootfs \
            initramfs-module-rootfs \
            initramfs-module-debug \
            initramfs-module-lvm \
            initramfs-module-overlayroot \
           "

FILES:${PN}-base = "/init /init.d/99-finish /dev"

# 99-finish in base depends on some other module which mounts
# the rootfs, like 90-rootfs. To replace that default, use
# BAD_RECOMMENDATIONS += "initramfs-module-rootfs" in your
# initramfs recipe and install something else, or install
# something that runs earlier (for example, a 89-my-rootfs)
# and mounts the rootfs. Then 90-rootfs will proceed immediately.
RRECOMMENDS:${PN}-base += "initramfs-module-rootfs"

SUMMARY:initramfs-module-exec = "initramfs support for easy execution of applications"
RDEPENDS:initramfs-module-exec = "${PN}-base"
FILES:initramfs-module-exec = "/init.d/89-exec"

SUMMARY:initramfs-module-mdev = "initramfs support for mdev"
RDEPENDS:initramfs-module-mdev = "${PN}-base busybox-mdev"
FILES:initramfs-module-mdev = "/init.d/01-mdev"

SUMMARY:initramfs-module-udev = "initramfs support for udev"
RDEPENDS:initramfs-module-udev = "${PN}-base udev"
FILES:initramfs-module-udev = "/init.d/01-udev"

SUMMARY:initramfs-module-e2fs = "initramfs support for ext4/ext3/ext2 filesystems"
RDEPENDS:initramfs-module-e2fs = "${PN}-base"
FILES:initramfs-module-e2fs = "/init.d/10-e2fs"

SUMMARY:initramfs-module-nfsrootfs = "initramfs support for locating and mounting the root partition via nfs"
RDEPENDS:initramfs-module-nfsrootfs = "${PN}-base"
FILES:initramfs-module-nfsrootfs = "/init.d/85-nfsrootfs"

SUMMARY:initramfs-module-rootfs = "initramfs support for locating and mounting the root partition"
RDEPENDS:initramfs-module-rootfs = "${PN}-base"
FILES:initramfs-module-rootfs = "/init.d/90-rootfs"

SUMMARY:initramfs-module-debug = "initramfs dynamic debug support"
RDEPENDS:initramfs-module-debug = "${PN}-base"
FILES:initramfs-module-debug = "/init.d/00-debug"

SUMMARY:initramfs-module-lvm = "initramfs lvm rootfs support"
RDEPENDS:initramfs-module-lvm = "${PN}-base"
FILES:initramfs-module-lvm = "/init.d/09-lvm"

SUMMARY:initramfs-module-overlayroot = "initramfs support for mounting a RW overlay on top of a RO root filesystem"
RDEPENDS:initramfs-module-overlayroot = "${PN}-base initramfs-module-rootfs"
FILES:initramfs-module-overlayroot = "/init.d/91-overlayroot"
