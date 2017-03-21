SUMMARY = "Phosphor Fan Presence"
DESCRIPTION = "Phosphor fan presence provides a set of fan presence \
daemons to monitor fan presence changes by different methods of \
presence detection."
HOMEPAGE = "https://github.com/openbmc/phosphor-fan-presence"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig pythonnative
inherit obmc-phosphor-systemd
inherit phosphor-fan-presence

DEPENDS += "autoconf-archive-native"
DEPENDS += "python-pyyaml-native"
DEPENDS += "python-mako-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"
DEPENDS += "virtual/phosphor-fan-presence-config"
RDEPENDS_${PN} += "sdbusplus"

FAN_PRESENCE_PACKAGES = " \
    ${PN}-tach \
"
PACKAGES_remove = "${PN}"
PACKAGES += "${FAN_PRESENCE_PACKAGES}"
SYSTEMD_PACKAGES = "${FAN_PRESENCE_PACKAGES}"
RDEPENDS_${PN}-dev = "${FAN_PRESENCE_PACKAGES}"

# Needed to install into the obmc-chassis-start target
TMPL = "phosphor-fan-presence-tach@.service"
INSTFMT = "phosphor-fan-presence-tach@{0}.service"
TGTFMT = "obmc-chassis-start@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${INSTFMT}"

FILES_${PN}-tach = "${sbindir}/phosphor-fan-presence-tach"
SYSTEMD_SERVICE_${PN}-tach += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"

SRC_URI += "git://github.com/openbmc/phosphor-fan-presence"
SRCREV = "6d9b18d5e51b269d741c13120a35a7b780ac8928"

S = "${WORKDIR}/git"

EXTRA_OECONF = \
    "FAN_DETECT_YAML_FILE=${STAGING_DIR_NATIVE}${presence_datadir}/config.yaml"
