SUMMARY = "Phosphor Fan"
DESCRIPTION = "Phosphor fan provides a set of fan monitoring and \
control applications."
PR = "r1"

require ${PN}.inc

inherit autotools pkgconfig pythonnative
inherit obmc-phosphor-systemd
inherit phosphor-fan

S = "${WORKDIR}/git"

# Common build dependencies
DEPENDS += "autoconf-archive-native"
DEPENDS += "python-pyyaml-native"
DEPENDS += "python-mako-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"

# Package configuration
FAN_PACKAGES = " \
        ${PN}-presence-tach \
        ${PN}-control \
        phosphor-chassis-cooling-type \
"
PACKAGES_remove = "${PN}"
PACKAGES += "${FAN_PACKAGES}"
PACKAGECONFIG ??= "presence control cooling-type"
SYSTEMD_PACKAGES = "${FAN_PACKAGES}"
RDEPENDS_${PN}-dev = "${FAN_PACKAGES}"
RDEPENDS_${PN}-staticdev = "${FAN_PACKAGES}"

# --------------------------------------
# ${PN}-presence-tach specific configuration
PACKAGECONFIG[presence] = " \
        --enable-presence \
        FAN_DETECT_YAML_FILE=${STAGING_DIR_NATIVE}${presence_datadir}/config.yaml, \
        --disable-presence, \
        virtual/phosphor-fan-presence-config \
        , \
"
RDEPENDS_${PN}-presence-tach += "sdbusplus"

# Needed to install into the obmc-host-start target
TMPL = "phosphor-fan-presence-tach@.service"
INSTFMT = "phosphor-fan-presence-tach@{0}.service"
TGTFMT = "obmc-host-start@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${INSTFMT}"

FILES_${PN}-presence-tach = "${sbindir}/phosphor-fan-presence-tach"
SYSTEMD_SERVICE_${PN}-presence-tach += "${TMPL}"
SYSTEMD_LINK_${PN}-presence-tach += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"

# --------------------------------------
# ${PN}-control specific configuration
PACKAGECONFIG[control] = "--enable-control,--disable-control,,"
FILES_${PN}-control = "${sbindir}/phosphor-fan-control"

# --------------------------------------
# phosphor-chassis-cooling-type specific configuration
PACKAGECONFIG[cooling-type] = "--enable-cooling-type,--disable-cooling-type,libevdev,"
RDEPENDS_phosphor-chassis-cooling-type += "libevdev"

COOLING_TMPL = "phosphor-cooling-type@.service"
COOLING_INSTFMT = "phosphor-cooling-type@{0}.service"
COOLING_TGT = "${SYSTEMD_DEFAULT_TARGET}"
COOLING_FMT = "../${COOLING_TMPL}:${COOLING_TGT}.requires/${COOLING_INSTFMT}"

FILES_phosphor-chassis-cooling-type = "${sbindir}/phosphor-cooling-type"
SYSTEMD_SERVICE_phosphor-chassis-cooling-type += "${COOLING_TMPL}"
SYSTEMD_LINK_phosphor-chassis-cooling-type += "${@compose_list(d, 'COOLING_FMT', 'OBMC_CHASSIS_INSTANCES')}"

