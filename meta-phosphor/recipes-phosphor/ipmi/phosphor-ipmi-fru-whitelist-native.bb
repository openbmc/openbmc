SUMMARY = "Whitelisted IPMI FRU Parser commands"
DESCRIPTION = "Whitelisted IPMI FRU Parser commands for OpenBMC"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit native
inherit phosphor-ipmi-host-whitelist
