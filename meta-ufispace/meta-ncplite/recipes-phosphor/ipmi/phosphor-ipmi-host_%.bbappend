FILESEXTRAPATHS:append:ncplite := "${THISDIR}/${PN}:"

DEPENDS:append:ncplite = " ncplite-yaml-config"

EXTRA_OEMESON:ncplite = " \
    -Dsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/ncplite-yaml-config/ipmi-sensors.yaml \
    -Dfru-yaml-gen=${STAGING_DIR_HOST}${datadir}/ncplite-yaml-config/ipmi-fru-read.yaml \
    -Dinvsensor-yaml-gen=${STAGING_DIR_HOST}${datadir}/ncplite-yaml-config/ipmi-inventory-sensors.yaml \
    "

RDEPENDS:${PN} += "bash"

SRC_URI += "file://phosphor-softpoweroff \
            file://xyz.openbmc_project.Ipmi.Internal.SoftPowerOff.service \
           "

do_install:append:ncplite (){
    install -m 0755 ${UNPACKDIR}/phosphor-softpoweroff ${D}/${bindir}/phosphor-softpoweroff
    install -m 0644 ${UNPACKDIR}/${SOFT_SVC} ${D}${systemd_unitdir}/system/${SOFT_SVC}
}
