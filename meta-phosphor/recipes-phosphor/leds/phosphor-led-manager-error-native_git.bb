SUMMARY = "Copy error yaml files to known path for elog parsing"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PV = "1.0+git${SRCPV}"
PR = "r1"

S = "${WORKDIR}/git"

inherit phosphor-dbus-yaml
inherit native

do_install:append() {
    SRC=${S}/xyz/openbmc_project/Led/
    DEST=${D}${yaml_dir}/xyz/openbmc_project/Led/
    install -d ${DEST}/Fru
    install ${SRC}/*.errors.yaml ${DEST}
    install ${SRC}/*.metadata.yaml ${DEST}
    install ${SRC}/Fru/*.errors.yaml ${DEST}/Fru
    install ${SRC}/Fru/*.metadata.yaml ${DEST}/Fru
}

require phosphor-led-manager.inc
