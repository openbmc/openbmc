SUMMARY = "initramfs-framework module for installation option"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
RDEPENDS_${PN} = "initramfs-framework-base grub parted e2fsprogs-mke2fs util-linux-blkid ${VIRTUAL-RUNTIME_base-utils}"

# The same restriction as grub
COMPATIBLE_HOST = '(x86_64.*|i.86.*|arm.*|aarch64.*)-(linux.*|freebsd.*)'
COMPATIBLE_HOST_armv7a = 'null'
COMPATIBLE_HOST_armv7ve = 'null'

PR = "r1"

SRC_URI = "file://init-install.sh"

S = "${WORKDIR}"

do_install() {
    install -d ${D}/init.d
    install -m 0755 ${WORKDIR}/init-install.sh ${D}/init.d/install.sh
}

FILES_${PN} = "/init.d/install.sh"
