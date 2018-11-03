SUMMARY = "Whitelisted IPMI FRU Parser commands"
DESCRIPTION = "Whitelisted IPMI FRU Parser commands for OpenBMC"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-ipmi-host-whitelist
