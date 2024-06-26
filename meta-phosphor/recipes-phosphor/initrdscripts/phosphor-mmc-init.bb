SUMMARY = "Phosphor OpenBMC pre-init scripts for mmc"
DESCRIPTION = "Phosphor OpenBMC filesystem mount implementation for mmc."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PR = "r1"

SRC_URI += "file://mmc-init.sh"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit allarch

do_install() {
    install -m 0755 ${UNPACKDIR}/mmc-init.sh ${D}/init
    install -d ${D}/dev
    mknod -m 622 ${D}/dev/console c 5 1
}

RDEPENDS:${PN} += " \
    ${@d.getVar('PREFERRED_PROVIDER_u-boot-fw-utils', True) or 'u-boot-fw-utils'} \
    ${VIRTUAL-RUNTIME_base-utils} \
    e2fsprogs-e2fsck \
    e2fsprogs-mke2fs \
    gptfdisk \
    libgpiod-tools \
    parted \
    udev \
"

FILES:${PN} += " /init /dev "
