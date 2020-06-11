SUMMARY = "phosphor-led-manager: install phosphor-logging yaml"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit phosphor-logging-yaml-provider
require phosphor-led-manager.inc

S = "${WORKDIR}/git"
