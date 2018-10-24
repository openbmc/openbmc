SUMMARY = "Copy error yaml files to known path for elog parsing"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-dbus-yaml

require phosphor-led-manager.inc

S = "${WORKDIR}/git"

do_install_append() {
    SRC=${S}/xyz/openbmc_project/Led/
    DEST=${D}${yaml_dir}/xyz/openbmc_project/Led/
    install -d ${DEST}/Fru
    install ${SRC}/*.errors.yaml ${DEST}
    install ${SRC}/*.metadata.yaml ${DEST}
    install ${SRC}/Fru/*.errors.yaml ${DEST}/Fru
    install ${SRC}/Fru/*.metadata.yaml ${DEST}/Fru
}
