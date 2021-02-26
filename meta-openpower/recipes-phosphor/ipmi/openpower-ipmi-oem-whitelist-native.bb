SUMMARY = "Whitelisted OpenBMC IPMI OEM commands"
DESCRIPTION = "Whitelisted OpenBMC IPMI OEM commands for OpenPOWER based systems"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit phosphor-ipmi-host-whitelist
inherit native
