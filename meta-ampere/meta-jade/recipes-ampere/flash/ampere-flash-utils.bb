
SUMMARY = "Ampere Computing LLC Flashing Utilities"
DESCRIPTION = "Application to support flashing utilities on Ampere platforms"
PR = "r0"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

RDEPENDS_${PN} = "bash"
DEPENDS = "zlib"

SRC_URI += "\
            file://ampere_eeprom_prog.c \
            file://nvparm.c \
            file://ampere_firmware_upgrade.sh \
            file://ampere_flash_bios.sh \
            file://ampere_fru_upgrade.c \
            "

S = "${WORKDIR}"
ROOT = "${STAGING_DIR_TARGET}"

LDFLAGS += "-L ${ROOT}/usr/lib/ -lz "

do_compile_append() {
    ${CC} ampere_eeprom_prog.c -o ampere_eeprom_prog -I${ROOT}/usr/include/ ${LDFLAGS}
    ${CC} nvparm.c -o nvparm -I${ROOT}/usr/include/ ${LDFLAGS}
    ${CC} ampere_fru_upgrade.c -o ampere_fru_upgrade -I${ROOT}/usr/include/ ${LDFLAGS}
}

do_install_append() {
    install -d ${D}/usr/sbin
    install -m 0755 ${S}/ampere_eeprom_prog ${D}/${sbindir}/ampere_eeprom_prog
    install -m 0755 ${S}/nvparm ${D}/${sbindir}/nvparm
    install -m 0755 ${S}/ampere_firmware_upgrade.sh ${D}/${sbindir}/ampere_firmware_upgrade.sh
    install -m 0755 ${S}/ampere_flash_bios.sh ${D}/${sbindir}/ampere_flash_bios.sh
    install -m 0755 ${S}/ampere_fru_upgrade ${D}/${sbindir}/ampere_fru_upgrade
}
