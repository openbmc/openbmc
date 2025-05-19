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

RDEPENDS:${PN} += " \
  bare-metal-active \
  "

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/google-ipmi-sys;branch=master;protocol=https"
SRCREV = "912963e1e9065a1f855689c0298308fefce8ac4b"

FILES:${PN} += "${libdir}/ipmid-providers"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += " \
  gbmc-host-poweroff.target \
  gbmc-psu-hardreset.target \
  gbmc-psu-hardreset-pre.target \
  gbmc-psu-hardreset-time.service \
  "

EXTRA_OEMESON += "-Dtests=disabled"

GBMC_NCSI_IPMI_CHANNEL ??= "1"

CXXFLAGS:append:gbmc = '${@"" if not d.getVar("GBMC_NCSI_IF_NAME") else \
  " -DNCSI_IPMI_CHANNEL=" + d.getVar("GBMC_NCSI_IPMI_CHANNEL") + " -DNCSI_IF_NAME=" + d.getVar("GBMC_NCSI_IF_NAME")}'
