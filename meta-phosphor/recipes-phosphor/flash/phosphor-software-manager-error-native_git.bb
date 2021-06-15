SUMMARY = "Copy error yaml files to known path for elog parsing"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit phosphor-dbus-yaml
inherit native

require phosphor-software-manager.inc

S = "${WORKDIR}/git"

do_install_append() {
    SRC=${S}/xyz/openbmc_project/Software/
    DEST=${D}${yaml_dir}/xyz/openbmc_project/Software/
    install -d ${DEST}
    install ${SRC}/*.errors.yaml ${DEST}
    install ${SRC}/*.metadata.yaml ${DEST}
}
