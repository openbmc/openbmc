SUMMARY = "Phosphor Fan Presence"
DESCRIPTION = "Phosphor fan presence provides a set of fan presence \
daemons to monitor fan presence changes by different methods of \
presence detection."
PR = "r1"

require ${PN}.inc

inherit autotools pkgconfig pythonnative
inherit obmc-phosphor-systemd
inherit phosphor-fan-presence
inherit phosphor-fan-control

DEPENDS += "autoconf-archive-native"
DEPENDS += "python-pyyaml-native"
DEPENDS += "python-mako-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"
DEPENDS += "virtual/phosphor-fan-presence-config"
DEPENDS += "virtual/phosphor-fan-control-fan-config"
DEPENDS += "virtual/phosphor-fan-control-zone-config"
DEPENDS += "libevdev"

FAN_PRESENCE_PACKAGES = " \
    ${PN}-tach \
"
PACKAGES_remove = "${PN}"
PACKAGES += "${FAN_PRESENCE_PACKAGES} phosphor-chassis-cooling-type \
             phosphor-fan-control"

# Remove when this package has content
ALLOW_EMPTY_phosphor-chassis-cooling-type = "1"

SYSTEMD_PACKAGES = "${FAN_PRESENCE_PACKAGES} phosphor-fan-control"
RDEPENDS_${PN}-dev = "${FAN_PRESENCE_PACKAGES} \
                      phosphor-chassis-cooling-type \
                      phosphor-fan-control"
RDEPENDS_${PN}-staticdev = "${FAN_PRESENCE_PACKAGES} \
                            phosphor-chassis-cooling-type \
                            phosphor-fan-control"

RDEPENDS_${PN}-tach += "sdbusplus"

RDEPENDS_phosphor-chassis-cooling-type += "libevdev"

# Needed to install into the obmc-chassis-start target
TMPL_TACH = "phosphor-fan-presence-tach@.service"
INSTFMT_TACH = "phosphor-fan-presence-tach@{0}.service"
TGTFMT = "obmc-chassis-start@{0}.target"
FMT_TACH = "../${TMPL_TACH}:${TGTFMT}.requires/${INSTFMT_TACH}"

TMPL_CONTROL = "phosphor-fan-control@.service"
INSTFMT_CONTROL = "phosphor-fan-control@{0}.service"
FMT_CONTROL = "../${TMPL_CONTROL}:${TGTFMT}.requires/${INSTFMT_CONTROL}"

FILES_${PN}-tach = "${sbindir}/phosphor-fan-presence-tach"
SYSTEMD_SERVICE_${PN}-tach += "${TMPL_TACH}"
SYSTEMD_LINK_${PN}-tach += "${@compose_list(d, 'FMT_TACH', 'OBMC_CHASSIS_INSTANCES')}"

FILES_phosphor-fan-control += "${sbindir}/phosphor-fan-control"
SYSTEMD_SERVICE_phosphor-fan-control += "${TMPL_CONTROL}"
SYSTEMD_LINK_phosphor-fan-control += "${@compose_list(d, 'FMT_CONTROL', 'OBMC_CHASSIS_INSTANCES')}"

S = "${WORKDIR}/git"

EXTRA_OECONF = \
    "FAN_DETECT_YAML_FILE=${STAGING_DIR_NATIVE}${presence_datadir}/config.yaml \
     FAN_DEF_YAML_FILE=${STAGING_DIR_NATIVE}${control_datadir}/fans.yaml \
     FAN_ZONE_YAML_FILE=${STAGING_DIR_NATIVE}${control_datadir}/zones.yaml"
