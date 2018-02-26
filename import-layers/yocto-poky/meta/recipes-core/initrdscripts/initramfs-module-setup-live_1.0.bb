SUMMARY = "initramfs-framework module for live booting"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
RDEPENDS_${PN} = "initramfs-framework-base udev-extraconf"

PR = "r4"

inherit allarch

FILESEXTRAPATHS_prepend := "${THISDIR}/initramfs-framework:"
SRC_URI = "file://setup-live"

S = "${WORKDIR}"

do_install() {
    install -d ${D}/init.d
    install -m 0755 ${WORKDIR}/setup-live ${D}/init.d/80-setup-live
}

FILES_${PN} = "/init.d/80-setup-live"
