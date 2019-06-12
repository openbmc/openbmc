SUMMARY = "Power supply policy configuration for meta-witherspoon machines"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${IBMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch
inherit phosphor-dbus-monitor

FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "file://power-supply-policy.yaml"

do_install() {
        install -D ${WORKDIR}/power-supply-policy.yaml ${D}${config_dir}/power-supply-policy.yaml
}

FILES_${PN} += "${config_dir}/power-supply-policy.yaml"
