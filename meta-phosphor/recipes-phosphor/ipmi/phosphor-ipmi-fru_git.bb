SUMMARY = "Phosphor IPMI Inventory Plugin"
DESCRIPTION = "A Phosphor IPMI plugin that updates inventory."
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit autotools pkgconfig
inherit obmc-phosphor-systemd
inherit obmc-phosphor-ipmiprovider-symlink
inherit phosphor-ipmi-fru
inherit pythonnative

require ${PN}.inc

DEPENDS += " \
        virtual/phosphor-ipmi-fru-inventory \
        virtual/phosphor-ipmi-fru-properties \
        systemd \
        sdbusplus \
        python-mako-native \
        python-pyyaml-native \
        phosphor-ipmi-host \
        phosphor-mapper \
        autoconf-archive-native \
        phosphor-logging \
        cli11 \
        "

SYSTEMD_SERVICE_${PN} += "obmc-read-eeprom@.service"

S = "${WORKDIR}/git"

HOSTIPMI_PROVIDER_LIBRARY += "libstrgfnhandler.so"

FILES_${PN}_append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV} ${libdir}/ipmid-providers/*.la"

EXTRA_OECONF = " \
             YAML_GEN=${STAGING_DIR_NATIVE}${config_datadir}/config.yaml \
             PROP_YAML=${STAGING_DIR_NATIVE}${properties_datadir}/extra-properties.yaml \
             "
