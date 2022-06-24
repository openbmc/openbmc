SUMMARY = "BIOS BMC SMM Error Logger"
DESCRIPTION = "Allows BIOS in SMM to log errors to the BMC"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d20ffc39dca1d714c75b4b6f0113be10"
inherit meson pkgconfig systemd

DEPENDS += " \
  boost \
  stdplus \
  systemd \
  nlohmann-json \
  libbej \
"

EXTRA_OEMESON = " \
  -Dtests=disabled \
"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/openbmc/bios-bmc-smm-error-logger;branch=main;protocol=https"
SRCREV = "1a3dc60db5f8764ea8b97fda5973ccbb894596df"

SYSTEMD_SERVICE:${PN} += "xyz.openbmc_project.bios_bmc_smm_error_logger.service"
