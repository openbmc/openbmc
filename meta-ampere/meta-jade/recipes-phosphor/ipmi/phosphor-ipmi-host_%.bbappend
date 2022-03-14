FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

DEPENDS:append = " ${MACHINE}-yaml-config"

RRECOMMENDS:${PN} += "ipmitool"
RDEPENDS:${PN} += "bash"

SRC_URI += " \
            file://ampere-phosphor-softpoweroff \
            file://ampere.xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service \
            "

EXTRA_OECONF = " \
                SENSOR_YAML_GEN=${STAGING_DIR_HOST}${datadir}/${MACHINE}-yaml-config/ipmi-sensors.yaml \
               "

AMPERE_SOFTPOWEROFF_TMPL = "ampere.xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service"

PACKAGECONFIG:append = " dynamic-sensors"
HOSTIPMI_PROVIDER_LIBRARY += "libdynamiccmds.so"

do_install:append(){
    install -d ${D}${includedir}/phosphor-ipmi-host
    install -m 0644 -D ${S}/selutility.hpp ${D}${includedir}/phosphor-ipmi-host
    install -m 0755 ${WORKDIR}/ampere-phosphor-softpoweroff ${D}/${bindir}/phosphor-softpoweroff
    install -m 0644 ${WORKDIR}/${AMPERE_SOFTPOWEROFF_TMPL} ${D}${systemd_unitdir}/system/xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service
}
