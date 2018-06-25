SUMMARY = "initramfs-framework module for EFI installation option"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
RDEPENDS_${PN} = "initramfs-framework-base parted e2fsprogs-mke2fs dosfstools util-linux-blkid ${VIRTUAL-RUNTIME_base-utils}"

PR = "r4"

SRC_URI = "file://init-install-efi.sh"

S = "${WORKDIR}"

do_install() {
    install -d ${D}/init.d
    install -m 0755 ${WORKDIR}/init-install-efi.sh ${D}/init.d/install-efi.sh
}

FILES_${PN} = "/init.d/install-efi.sh"
