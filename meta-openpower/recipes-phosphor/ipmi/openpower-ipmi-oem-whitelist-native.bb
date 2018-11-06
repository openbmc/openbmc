SUMMARY = "Whitelisted OpenBMC IPMI OEM commands"
DESCRIPTION = "Whitelisted OpenBMC IPMI OEM commands for OpenPOWER based systems"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${OPENPOWERBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-ipmi-host-whitelist
