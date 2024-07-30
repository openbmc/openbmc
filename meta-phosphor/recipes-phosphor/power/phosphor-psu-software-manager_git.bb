# The below configs are expected to be overriden by machine layer
SUMMARY = "Phosphor PSU software manager"
DESCRIPTION = "Providing PSU firmware version and upgrade"
HOMEPAGE = "https://github.com/openbmc/phosphor-psu-code-mgmt"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
DEPENDS = " \
         phosphor-logging \
         phosphor-dbus-interfaces \
         sdbusplus \
         openssl \
         "
SRCREV = "935f551c5557087bc96b1a30bab2891ca87a6334"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-psu-code-mgmt;branch=master;protocol=https"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} = "xyz.openbmc_project.Software.Psu.Updater.service"
SYSTEMD_SERVICE:${PN} += "psu-update@.service"

inherit meson
inherit pkgconfig
inherit obmc-phosphor-systemd

EXTRA_OEMESON = " \
        -Dtests=disabled \
        ${PSU_VERSION_UTIL} \
        ${PSU_VERSION_COMPARE_UTIL} \
        ${PSU_UPDATE_SERVICE} \
        "

# The default config of this repo depends on utils from phosphor-power.
# If your system does not depend on phosphor-power, please use
# RDEPENDS:${PN}:remove to remove the dependency.
RDEPENDS:${PN} += "phosphor-power"

## The psutils here comes from phosphor-power repo where
## * PSU_VERSION_UTIL accepts a PSU inventory path and returns the PSU
##   firmware version string
## * PSU_VERSION_COMPARE_UTIL accepts several PSU inventory paths and return
##   the newest version string
PSU_VERSION_UTIL ?= "-DPSU_VERSION_UTIL='/usr/bin/psutils --raw --get-version'"
PSU_VERSION_COMPARE_UTIL ?= "-DPSU_VERSION_COMPARE_UTIL='/usr/bin/psutils --raw --compare'"
## The psu-update@.service from repo is an example service that only prints a log and fails
## Override it in a machine layer to invoke the psu update util
PSU_UPDATE_SERVICE ?= "-DPSU_UPDATE_SERVICE=psu-update@.service"
