SUMMARY = "BIOS BMC SMM Error Logger"
DESCRIPTION = "Allows BIOS in SMM to log errors to the BMC"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
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
SRCREV = "e8cac46964255e6d4f7359045165fc47786746ce"

SYSTEMD_SERVICE:${PN} += "xyz.openbmc_project.bios_bmc_smm_error_logger.service"
