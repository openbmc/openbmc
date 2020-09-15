SUMMARY = "Entity Manager"
DESCRIPTION = "Entity Manager provides d-bus configuration data \
and configures system sensors"

SRC_URI = "git://github.com/openbmc/entity-manager.git file://blocklist.json"
SRCREV = "844bf77aa5e208779f28e504b1d3a9532e5cacf7"
PV = "0.1+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=a6a4edad4aed50f39a66d098d74b265b"

DEPENDS = "boost \
           nlohmann-json \
           sdbusplus \
           valijson"

S = "${WORKDIR}/git/"
inherit meson systemd

EXTRA_OEMESON = "-Dtests=disabled"

PACKAGECONFIG ??= "ipmi-fru"
PACKAGECONFIG[ipmi-fru] = "-Dfru-device=true, -Dfru-device=false, i2c-tools,"

EXTRA_ENTITY_MANAGER_PACKAGES = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'ipmi-fru', 'fru-device', '', d)} \
    "

PACKAGE_BEFORE_PN = "${EXTRA_ENTITY_MANAGER_PACKAGES}"

do_install_append() {
    install -D ${WORKDIR}/blocklist.json ${D}${datadir}/${BPN}/blacklist.json
}

FILES_fru-device = "${bindir}/fru-device ${datadir}/${BPN}/blacklist.json"

SYSTEMD_PACKAGES = "${PN} ${EXTRA_ENTITY_MANAGER_PACKAGES}"
SYSTEMD_SERVICE_${PN} = "xyz.openbmc_project.EntityManager.service"
SYSTEMD_SERVICE_fru-device = "xyz.openbmc_project.FruDevice.service"
SYSTEMD_AUTO_ENABLE_fru-device_ibm-power-cpu = "disable"
