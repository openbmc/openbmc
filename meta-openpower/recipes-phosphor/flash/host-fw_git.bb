SUMMARY = "OpenPOWER Host Firmware Image"
DESCRIPTION = "Adds the OpenPOWER Host Firmware image to the BMC image"
PR = "r1"

inherit allarch

HOST_FW_LICENSE ?= "Apache-2.0"
HOST_FW_LIC_FILES_CHKSUM ?= "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
HOST_FW_SRC_URI ?= ""

LICENSE = "${HOST_FW_LICENSE}"
LIC_FILES_CHKSUM = "${HOST_FW_LIC_FILES_CHKSUM}"
SRC_URI = "${HOST_FW_SRC_URI}"

DEPENDS = "squashfs-tools-native"

S = "${WORKDIR}"
B = "${WORKDIR}/build"
do_compile[cleandirs] = "${B}"

do_compile() {
    if [ -n "${HOST_FW_SRC_URI}" ]; then
        unsquashfs -d ${B}/squashfs-root ${S}/pnor.xz.squashfs
    fi
}

do_install() {
    install -d ${D}${datadir}/${BPN}

    if [ -n "${HOST_FW_SRC_URI}" ]; then
        install -m 0440 ${B}/squashfs-root/* ${D}${datadir}/${BPN}
    fi
}
