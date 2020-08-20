SUMMARY = "Entity Manager"
DESCRIPTION = "Entity Manager provides d-bus configuration data \
and configures system sensors"

SRC_URI = "git://github.com/openbmc/entity-manager.git"
SRCREV = "4212485fbe2e8907a6a924bee5729be1539db2c0"
PV = "0.1+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=a6a4edad4aed50f39a66d098d74b265b"

SYSTEMD_SERVICE_${PN} = "xyz.openbmc_project.EntityManager.service \
                         ${@bb.utils.contains('DISTRO_FEATURES', 'ipmi-fru', 'xyz.openbmc_project.FruDevice.service', '', d)}"

DEPENDS = "boost \
           nlohmann-json \
           sdbusplus \
           valijson"

S = "${WORKDIR}/git/"
inherit meson systemd

EXTRA_OEMESON = "-Dtests=disabled"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipmi-fru', d)}"
PACKAGECONFIG[ipmi-fru] = "-Dfru-device=true, -Dfru-device=false, i2c-tools,"
