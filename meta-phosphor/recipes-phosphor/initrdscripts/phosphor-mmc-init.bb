SUMMARY = "Phosphor OpenBMC pre-init scripts for mmc"
DESCRIPTION = "Phosphor OpenBMC filesystem mount implementation for mmc."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PR = "r1"

inherit allarch

RDEPENDS_${PN} += " \
    ${VIRTUAL-RUNTIME_base-utils} \
    e2fsprogs-e2fsck \
    gptfdisk \
    parted \
    udev \
"

S = "${WORKDIR}"
SRC_URI += "file://mmc-init.sh"

do_install() {
    install -m 0755 ${WORKDIR}/mmc-init.sh ${D}/init
    install -d ${D}/dev
    mknod -m 622 ${D}/dev/console c 5 1
}

FILES_${PN} += " /init /dev "
