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
"
SRCREV = "2ec1f15f33d83e9bddb5e3318a6d94d4a92ddc90"
PACKAGECONFIG ??= "ipmi-fru"
PACKAGECONFIG[ipmi-fru] = "-Dfru-device=true, -Dfru-device=false, i2c-tools,"
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
SYSTEMD_AUTO_ENABLE:fru-device:ibm-power-cpu = "disable"

inherit pkgconfig meson systemd python3native

EXTRA_OEMESON = "-Dtests=disabled"
EXTRA_ENTITY_MANAGER_PACKAGES = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'ipmi-fru', 'fru-device', '', d)} \
    "

do_install:append() {
    install -D ${WORKDIR}/blocklist.json ${D}${datadir}/${BPN}/blacklist.json
}

FILES:${PN} += " \
    ${datadir}/dbus-1/system-services/xyz.openbmc_project.EntityManager.service \
    "
FILES:fru-device = "${bindir}/fru-device ${datadir}/${BPN}/blacklist.json"

PACKAGE_BEFORE_PN = "${EXTRA_ENTITY_MANAGER_PACKAGES}"
