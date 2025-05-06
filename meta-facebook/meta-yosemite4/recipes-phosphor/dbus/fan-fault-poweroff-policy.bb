SUMMARY = "Fan fail mode action for meta-yosemite4 machines"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-dbus-monitor

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " \
    file://fan-rotor-fail-poweroff.yaml \
"

do_install() {
    install -D ${UNPACKDIR}/fan-rotor-fail-poweroff.yaml ${D}${config_dir}/fan-rotor-fail-poweroff.yaml
}

FILES:${PN}:append = " \
    ${config_dir}/fan-rotor-fail-poweroff.yaml \
"