SUMMARY = "Entity Manager"
DESCRIPTION = "Entity Manager provides d-bus configuration data \
and configures system sensors"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=a6a4edad4aed50f39a66d098d74b265b"
DEPENDS = "boost \
           dbus \
           nlohmann-json \
           phosphor-logging \
           sdbusplus \
           valijson \
           phosphor-dbus-interfaces \
"
SRCREV = "14debea1086b867d718a9cdfca0d9f52b6bd8bce"
PACKAGECONFIG ??= "ipmi-fru"

PACKAGECONFIG[ipmi-fru] = "-Dfru-device=true, -Dfru-device=false, i2c-tools,"
PACKAGECONFIG[dts-vpd] = "-Ddevicetree-vpd=true, -Ddevicetree-vpd=false"
PACKAGECONFIG[validate-json] = "-Dvalidate-json=true, \
                                -Dvalidate-json=false, \
                                ${PYTHON_PN}-jsonschema-native"
PV = "0.1+git${SRCPV}"

SRC_URI = "git://github.com/openbmc/entity-manager.git;branch=master;protocol=https \
           file://blocklist.json \
          "

S = "${WORKDIR}/git"
SYSTEMD_PACKAGES = "${PN} ${EXTRA_ENTITY_MANAGER_PACKAGES}"
SYSTEMD_SERVICE:${PN} = "xyz.openbmc_project.EntityManager.service"
SYSTEMD_SERVICE:fru-device = "xyz.openbmc_project.FruDevice.service"
SYSTEMD_SERVICE:devicetree-vpd = "devicetree-vpd-parser.service"
SYSTEMD_AUTO_ENABLE:fru-device:ibm-power-cpu = "disable"

inherit pkgconfig meson systemd python3native

EXTRA_OEMESON = "-Dtests=disabled"
EXTRA_ENTITY_MANAGER_PACKAGES = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'ipmi-fru', 'fru-device', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'dts-vpd', 'devicetree-vpd', '', d)} \
    "

do_install:append() {
    install -D ${WORKDIR}/blocklist.json ${D}${datadir}/${BPN}/blacklist.json
}

FILES:${PN} += " \
    ${datadir}/dbus-1/system-services/xyz.openbmc_project.EntityManager.service \
    "
FILES:fru-device = "${bindir}/fru-device ${datadir}/${BPN}/blacklist.json"
FILES:devicetree-vpd = "${bindir}/devicetree-vpd-parser"

RRECOMMENDS:${PN} = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'ipmi-fru', 'fru-device', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'dts-vpd', 'devicetree-vpd', '', d)} \
    "

PACKAGE_BEFORE_PN = "${EXTRA_ENTITY_MANAGER_PACKAGES}"
