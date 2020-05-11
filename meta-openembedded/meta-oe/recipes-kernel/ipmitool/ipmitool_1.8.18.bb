SUMMARY = "Utility for IPMI control"
DESCRIPTION = "This package contains a utility for interfacing with devices that support \
the Intelligent Platform Management Interface specification. IPMI is \
an open standard for machine health, inventory, and remote power control. \
\
This utility can communicate with IPMI-enabled devices through either a \
kernel driver such as OpenIPMI or over the RMCP LAN protocol defined in \
the IPMI specification. IPMIv2 adds support for encrypted LAN \
communications and remote Serial-over-LAN functionality. \
\
It provides commands for reading the Sensor Data Repository (SDR) and \
displaying sensor values, displaying the contents of the System Event \
Log (SEL), printing Field Replaceable Unit (FRU) information, reading and \
setting LAN configuration, and chassis power control. \
"

HOMEPAGE = "http://ipmitool.sourceforge.net/"
SECTION = "kernel/userland"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=9aa91e13d644326bf281924212862184"

DEPENDS = "openssl readline ncurses"

SRC_URI = "${SOURCEFORGE_MIRROR}/ipmitool/ipmitool-${PV}.tar.bz2 \
           file://0001-Migrate-to-openssl-1.1.patch \
           file://0001-fru-Fix-buffer-overflow-vulnerabilities.patch \
           file://0001-fru-Fix-buffer-overflow-in-ipmi_spd_print_fru.patch \
           file://0002-session-Fix-buffer-overflow-in-ipmi_get_session_info.patch \
           file://0003-channel-Fix-buffer-overflow.patch \
           file://0004-lanp-Fix-buffer-overflows-in-get_lan_param_select.patch \
           file://0005-fru-sdr-Fix-id_string-buffer-overflows.patch \
           "
SRC_URI[md5sum] = "bab7ea104c7b85529c3ef65c54427aa3"
SRC_URI[sha256sum] = "0c1ba3b1555edefb7c32ae8cd6a3e04322056bc087918f07189eeedfc8b81e01"

inherit autotools

# --disable-dependency-tracking speeds up the build
# --enable-file-security adds some security checks
# --disable-intf-free disables FreeIPMI support - we don't want to depend on
#   FreeIPMI libraries, FreeIPMI has its own ipmitoool-like utility.
#
EXTRA_OECONF = "--disable-dependency-tracking --enable-file-security --disable-intf-free"
