SUMMARY = "Google Sys OEM commands"
DESCRIPTION = "Google Sys OEM commands"
HOMEPAGE = "https://github.com/openbmc/google-ipmi-sys"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit meson pkgconfig systemd

DEPENDS += " \
  nlohmann-json \
  phosphor-dbus-interfaces \
  phosphor-logging \
  phosphor-ipmi-host \
  sdbusplus \
  systemd \
  "

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/google-ipmi-sys;branch=master;protocol=https"
SRCREV = "40fe52225515f6ec82858795638add0b4c8e94af"

FILES:${PN} += "${libdir}/ipmid-providers"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += " \
  gbmc-host-poweroff.target \
  gbmc-psu-hardreset.target \
  "

EXTRA_OEMESON += "-Dtests=disabled"

CXXFLAGS:append:gbmc = '${@"" if not d.getVar("GBMC_NCSI_IF_NAME") else \
  " -DNCSI_IPMI_CHANNEL=1 -DNCSI_IF_NAME=" + d.getVar("GBMC_NCSI_IF_NAME")}'
