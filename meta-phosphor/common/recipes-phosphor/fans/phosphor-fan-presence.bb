SUMMARY = "Phosphor Fan Presence"
DESCRIPTION = "Phosphor fan presence provides a set of fan presence \
daemons to monitor fan presence changes by different methods of \
presence detection."
PR = "r1"

require ${PN}.inc

inherit autotools pkgconfig pythonnative
inherit obmc-phosphor-systemd
inherit phosphor-fan-presence

DEPENDS += "autoconf-archive-native"
DEPENDS += "python-pyyaml-native"
DEPENDS += "python-mako-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"
DEPENDS += "virtual/phosphor-fan-presence-config"
DEPENDS += "libevdev"

FAN_PRESENCE_PACKAGES = " \
    ${PN}-tach \
"
PACKAGES_remove = "${PN}"
PACKAGES += "${FAN_PRESENCE_PACKAGES} phosphor-chassis-cooling-type"

# Remove when this package has content
ALLOW_EMPTY_phosphor-chassis-cooling-type = "1"

SYSTEMD_PACKAGES = "${FAN_PRESENCE_PACKAGES}"
RDEPENDS_${PN}-dev = "${FAN_PRESENCE_PACKAGES} phosphor-chassis-cooling-type"

RDEPENDS_${PN}-tach += "sdbusplus"

RDEPENDS_phosphor-chassis-cooling-type += "libevdev"

# Needed to install into the obmc-host-start target
TMPL = "phosphor-fan-presence-tach@.service"
INSTFMT = "phosphor-fan-presence-tach@{0}.service"
TGTFMT = "obmc-host-start@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${INSTFMT}"

FILES_${PN}-tach = "${sbindir}/phosphor-fan-presence-tach"
SYSTEMD_SERVICE_${PN}-tach += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"

S = "${WORKDIR}/git"

EXTRA_OECONF = \
    "FAN_DETECT_YAML_FILE=${STAGING_DIR_NATIVE}${presence_datadir}/config.yaml"
