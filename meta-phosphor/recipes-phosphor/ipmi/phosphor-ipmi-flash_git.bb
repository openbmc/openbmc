HOMEPAGE = "http://github.com/openbmc/phosphor-ipmi-flash"
SUMMARY = "Phosphor OEM IPMI In-band Firmware Update over BLOB"
DESCRIPTION = "This package handles a series of OEM IPMI commands that implement the firmware update handler over the BLOB protocol."
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit autotools pkgconfig
inherit obmc-phosphor-ipmiprovider-symlink
inherit systemd

DEPENDS += "autoconf-archive-native"
DEPENDS += "phosphor-ipmi-blobs"
DEPENDS += "phosphor-logging"
DEPENDS += "sdbusplus"
DEPENDS += "systemd"
DEPENDS += "ipmi-blob-tool"
DEPENDS += "pciutils"

PACKAGECONFIG ?= "cleanup-delete"
PACKAGECONFIG[cleanup-delete] = "--enable-cleanup-delete, --disable-cleanup-delete"
# If using static-layout, reboot-update is a good option to handle updating.
# To be able to track the update status, update-status option can be used.
# Note that both reboot-update and update-status cannot be enabled at the same time.
PACKAGECONFIG[reboot-update] = "--enable-reboot-update, --disable-reboot-update"
PACKAGECONFIG[update-status] = "--enable-update-status, --disable-update-status"

# Default options for supporting various flash types:
PACKAGECONFIG[static-bmc] = "--enable-static-layout, --disable-static-layout"
PACKAGECONFIG[ubitar-bmc] = "--enable-tarball-ubi, --disable-tarball-ubi"
PACKAGECONFIG[host-bios] = "--enable-host-bios, --disable-host-bios"

# Hardware options to enable transmitting the data from the host.
PACKAGECONFIG[aspeed-p2a] = "--enable-aspeed-p2a, --disable-aspeed-p2a"
PACKAGECONFIG[aspeed-lpc] = "--enable-aspeed-lpc, --disable-aspeed-lpc"
PACKAGECONFIG[nuvoton-lpc] = "--enable-nuvoton-lpc, --disable-nuvoton-lpc"
PACKAGECONFIG[net-bridge] = "--enable-net-bridge, --disable-net-bridge"

EXTRA_OECONF = "--disable-tests --disable-build-host-tool"

# Set this variable in your recipe to set it instead of using MAPPED_ADDRESS directly.
IPMI_FLASH_BMC_ADDRESS ?= "0"
EXTRA_OECONF_append = " MAPPED_ADDRESS=${IPMI_FLASH_BMC_ADDRESS}"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/phosphor-ipmi-flash"
SRCREV = "6f61af8fd125400d064715559cfffba92afaecf6"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} += "phosphor-ipmi-flash-bmc-prepare.target \
 phosphor-ipmi-flash-bmc-verify.target \
 phosphor-ipmi-flash-bmc-update.target"

# If they enabled host-bios, add those three extra targets.
HOST_BIOS_TARGETS = "phosphor-ipmi-flash-bios-prepare.target \
 phosphor-ipmi-flash-bios-verify.target \
 phosphor-ipmi-flash-bios-update.target"

SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'host-bios', '${HOST_BIOS_TARGETS}', '', d)}"

FILES_${PN}_append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/blob-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV} ${libdir}/ipmid-providers/*.la"

BLOBIPMI_PROVIDER_LIBRARY += "libfirmwareblob.so"
BLOBIPMI_PROVIDER_LIBRARY += "${@bb.utils.contains('PACKAGECONFIG', 'cleanup-delete', 'libfirmwarecleanupblob.so', '', d)}"

do_configure[depends] += "virtual/kernel:do_shared_workdir"
