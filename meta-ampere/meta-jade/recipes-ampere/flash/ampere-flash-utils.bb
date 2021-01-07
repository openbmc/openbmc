
SUMMARY = "Ampere Computing LLC Flashing Utilities"
DESCRIPTION = "Application to support flashing utilities on Ampere platforms"
PR = "r0"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

RDEPENDS_${PN} = "bash"
DEPENDS = "zlib"

SRC_URI += "\
            file://ampere_flash_bios.sh \
           "

S = "${WORKDIR}"
ROOT = "${STAGING_DIR_TARGET}"

LDFLAGS += "-L ${ROOT}/usr/lib/ -lz "

do_install_append() {
    install -d ${D}/usr/sbin
    install -m 0755 ${S}/ampere_flash_bios.sh ${D}/${sbindir}/ampere_flash_bios.sh
}
