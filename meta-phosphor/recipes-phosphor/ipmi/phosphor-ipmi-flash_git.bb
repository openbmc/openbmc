SUMMARY = "Phosphor OEM IPMI In-band Firmware Update over BLOB"
DESCRIPTION = "This package handles a series of OEM IPMI commands that implement the firmware update handler over the BLOB protocol."
HOMEPAGE = "http://github.com/openbmc/phosphor-ipmi-flash"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
DEPENDS += " \
  phosphor-ipmi-blobs \
  phosphor-logging \
  sdbusplus \
  systemd \
  ipmi-blob-tool \
"
SRCREV = "0c9edcfedabdb64a049d1e2ca705da860cb37955"
PACKAGECONFIG ?= "cleanup-delete"
PACKAGECONFIG[cleanup-delete] = "-Dcleanup-delete=enabled,-Dcleanup-delete=disabled"
# If using static-layout, reboot-update is a good option to handle updating.
# To be able to track the update status, update-status option can be used.
# Note that both reboot-update and update-status cannot be enabled at the same time.
PACKAGECONFIG[reboot-update] = "-Dreboot-update=true,-Dreboot-update=false"
PACKAGECONFIG[update-status] = "-Dupdate-status=true,-Dupdate-status=false"
# Default options for supporting various flash types:
PACKAGECONFIG[static-bmc] = "-Dupdate-type=static-layout,-Dupdate-type=none"
PACKAGECONFIG[ubitar-bmc] = "-Dupdate-type=tarball-ubi,-Dupdate-type=none"
PACKAGECONFIG[host-bios] = "-Dhost-bios=true,-Dhost-bios=false"
# Hardware options to enable transmitting the data from the host.
# Only one type of p2a or lpc can be enabled.
PACKAGECONFIG[aspeed-p2a] = "-Dp2a-type=aspeed-p2a,,,,,aspeed-lpc nuvoton-lpc nuvoton-p2a-vga nuvoton-p2a-mbox"
PACKAGECONFIG[aspeed-lpc] = "-Dlpc-type=aspeed-lpc,,,,,aspeed-p2a nuvoton-lpc nuvoton-p2a-vga nuvoton-p2a-mbox"
PACKAGECONFIG[nuvoton-lpc] = "-Dlpc-type=nuvoton-lpc,,,,,aspeed-p2a aspeed-lpc nuvoton-p2a-vga nuvoton-p2a-mbox"
PACKAGECONFIG[nuvoton-p2a-vga] = "-Dp2a-type=nuvoton-p2a-vga,,,,,aspeed-p2a aspeed-lpc nuvoton-lpc nuvoton-p2a-mbox"
PACKAGECONFIG[nuvoton-p2a-mbox] = "-Dp2a-type=nuvoton-p2a-mbox,,,,,aspeed-p2a aspeed-lpc nuvoton-lpc nuvoton-p2a-vga"
PACKAGECONFIG[net-bridge] = "-Dnet-bridge=true,-Dnet-bridge=false"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-ipmi-flash;branch=master;protocol=https"

S = "${WORKDIR}/git"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += " \
  phosphor-ipmi-flash-bmc-prepare.target \
  phosphor-ipmi-flash-bmc-verify.target \
  phosphor-ipmi-flash-bmc-update.target \
"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'host-bios', '${HOST_BIOS_TARGETS}', '', d)}"

inherit meson pkgconfig systemd

EXTRA_OEMESON = "-Dtests=disabled -Dhost-tool=disabled"
EXTRA_OEMESON:append = " -Dmapped-address=${IPMI_FLASH_BMC_ADDRESS}"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

FILES:${PN}:append = " ${libdir}/ipmid-providers"
FILES:${PN}:append = " ${libdir}/blob-ipmid"
FILES:${PN}:append = " ${libdir}/tmpfiles.d"

BLOBIPMI_PROVIDER_LIBRARY += "libfirmwareblob.so"
BLOBIPMI_PROVIDER_LIBRARY += "libversionblob.so"
BLOBIPMI_PROVIDER_LIBRARY += "liblogblob.so"
BLOBIPMI_PROVIDER_LIBRARY += "${@bb.utils.contains('PACKAGECONFIG', 'cleanup-delete', 'libfirmwarecleanupblob.so', '', d)}"

# Set this variable in your recipe to set it instead of using MAPPED_ADDRESS directly.
IPMI_FLASH_BMC_ADDRESS ?= "0"
# If they enabled host-bios, add those three extra targets.
HOST_BIOS_TARGETS = " \
  phosphor-ipmi-flash-bios-prepare.target \
  phosphor-ipmi-flash-bios-verify.target \
  phosphor-ipmi-flash-bios-update.target \
"
