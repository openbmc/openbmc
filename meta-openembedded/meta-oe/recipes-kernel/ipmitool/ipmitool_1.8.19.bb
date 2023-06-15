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
SRCREV = "19d78782d795d0cf4ceefe655f616210c9143e62"
SRC_URI = "git://github.com/ipmitool/ipmitool;protocol=https;branch=master \
           ${IANA_ENTERPRISE_NUMBERS} \
           file://0001-ipmi_fru.c-Provide-missing-function-declarations.patch \
           file://0001-configure-Remove-the-logic-to-download-IANA-PEN-data.patch \
           "
IANA_ENTERPRISE_NUMBERS ?= ""

# Add these via bbappend if this database is needed by the system
#IANA_ENTERPRISE_NUMBERS = "http://www.iana.org/assignments/enterprise-numbers;name=iana-enterprise-numbers;downloadfilename=iana-enterprise-numbers"
#SRC_URI[iana-enterprise-numbers.sha256sum] = "cdd97fc08325667434b805eb589104ae63f7a9eb720ecea73cb55110b383934c"

S = "${WORKDIR}/git"

inherit autotools

do_install:append() {
        if [ -e ${WORKDIR}/iana-enterprise-numbers ]; then
                install -Dm 0755 ${WORKDIR}/iana-enterprise-numbers ${D}${datadir}/misc/enterprise-numbers
        fi
}

PACKAGES =+ "${PN}-ipmievd"
FILES:${PN}-ipmievd += "${sbindir}/ipmievd"
FILES:${PN} += "${datadir}/misc"

# --disable-dependency-tracking speeds up the build
# --enable-file-security adds some security checks
# --disable-intf-free disables FreeIPMI support - we don't want to depend on
#   FreeIPMI libraries, FreeIPMI has its own ipmitoool-like utility.
#
EXTRA_OECONF = "--disable-dependency-tracking --enable-file-security --disable-intf-free"
