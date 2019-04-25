SUMMARY = "Meta-initramfs packagegroups"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = ' \
    packagegroup-meta-initramfs \
    packagegroup-meta-initramfs-devtools \
    packagegroup-meta-initramfs-kernel \
'

RDEPENDS_packagegroup-meta-initramfs = "\
    packagegroup-meta-initramfs-devtools \
    packagegroup-meta-initramfs-kernel \
"

RDEPENDS_packagegroup-meta-initramfs-devtools = "\
    libklibc  dracut \
    klibc-utils-cat klibc-utils-losetup klibc-utils-readlink klibc-utils-chroot \
    klibc-utils-ls klibc-utils-reboot klibc-utils-cpio klibc-utils-minips \
    klibc-utils-resume klibc-utils-dd klibc-utils-mkdir klibc-utils-run-init \
    klibc-utils-dmesg klibc-utils-mkfifo klibc-utils-sh.shared klibc-utils-false \
    klibc-utils-mknod klibc-utils-sleep klibc-utils-fstype klibc-utils-mount \
    klibc-utils-sync klibc-utils-halt klibc-utils-mv klibc-utils-true klibc-utils-ipconfig \
    klibc-utils-nfsmount klibc-utils-umount klibc-utils-kill klibc-utils-nuke klibc-utils-uname \
    mtdinfo-klibc ubiattach-klibc ubiblock-klibc ubicrc32-klibc ubidetach-klibc \
    ubiformat-klibc ubimkvol-klibc ubinfo-klibc ubinize-klibc ubirename-klibc \
    ubirmvol-klibc ubirsvol-klibc ubiupdatevol-klibc \
    ${@bb.utils.contains_any("TRANSLATED_TARGET_ARCH", "i586 x86-64", "grubby", "", d)} \
    "

RDEPENDS_packagegroup-meta-initramfs-kernel = "\
    kexec-klibc vmcore-dmesg-klibc \
    "

EXCLUDE_FROM_WORLD = "1"
