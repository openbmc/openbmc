SUMMARY = "initramfs-framework module for installation option"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
RDEPENDS:${PN} = "initramfs-framework-base grub parted e2fsprogs-mke2fs util-linux-blkid ${VIRTUAL-RUNTIME_base-utils}"
RRECOMMENDS:${PN} = "${VIRTUAL-RUNTIME_base-utils-syslog}"

# The same restriction as grub
COMPATIBLE_HOST = '(x86_64.*|i.86.*|arm.*|aarch64.*|loongarch64.*)-(linux.*|freebsd.*)'
COMPATIBLE_HOST:armv7a = 'null'
COMPATIBLE_HOST:armv7ve = 'null'


SRC_URI = "file://init-install.sh"

S = "${WORKDIR}"

do_install() {
    install -d ${D}/init.d
    install -m 0755 ${WORKDIR}/init-install.sh ${D}/init.d/install.sh
}

FILES:${PN} = "/init.d/install.sh"
