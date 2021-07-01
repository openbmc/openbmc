FILESEXTRAPATHS_append_mtjade := "${THISDIR}/${PN}:"

DEPENDS_append_mtjade = " mtjade-yaml-config"

RRECOMMENDS_${PN} += "ipmitool"
RDEPENDS_${PN} += "bash"

SRC_URI += " \
            file://ampere-phosphor-softpoweroff \
            file://ampere.xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service \
            "

EXTRA_OECONF_mtjade = " \
    SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/mtjade-yaml-config/ipmi-sensors-${MACHINE}.yaml \
    FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/mtjade-yaml-config/ipmi-fru-read.yaml \
    "

AMPERE_SOFTPOWEROFF_TMPL = "ampere.xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service"

do_install_append_mtjade(){
    install -d ${D}${includedir}/phosphor-ipmi-host
    install -m 0644 -D ${S}/selutility.hpp ${D}${includedir}/phosphor-ipmi-host
    install -m 0755 ${WORKDIR}/ampere-phosphor-softpoweroff ${D}/${bindir}/phosphor-softpoweroff
    install -m 0644 ${WORKDIR}/${AMPERE_SOFTPOWEROFF_TMPL} ${D}${systemd_unitdir}/system/xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service
}
