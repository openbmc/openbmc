SUMMARY = "Entity Manager"
DESCRIPTION = "Entity Manager provides d-bus configuration data \
and configures system sensors"

SRC_URI = "git://github.com/openbmc/entity-manager.git file://blocklist.json"
SRCREV = "c76af0fb1defcc9cc3d000c1ca7592d0f01df2a1"
PV = "0.1+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=a6a4edad4aed50f39a66d098d74b265b"

DEPENDS = "boost \
           dbus \
           nlohmann-json \
           sdbusplus \
           valijson"

S = "${WORKDIR}/git"
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

FILES_${PN} += " \
    ${datadir}/dbus-1/system-services/xyz.openbmc_project.EntityManager.service \
    "
FILES_fru-device = "${bindir}/fru-device ${datadir}/${BPN}/blacklist.json"

SYSTEMD_PACKAGES = "${PN} ${EXTRA_ENTITY_MANAGER_PACKAGES}"
SYSTEMD_SERVICE_${PN} = "xyz.openbmc_project.EntityManager.service"
SYSTEMD_SERVICE_fru-device = "xyz.openbmc_project.FruDevice.service"
SYSTEMD_AUTO_ENABLE_fru-device_ibm-power-cpu = "disable"
