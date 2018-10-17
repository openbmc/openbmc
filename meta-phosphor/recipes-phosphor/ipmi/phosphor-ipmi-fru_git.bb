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
        virtual/phosphor-ipmi-fru-hostfw-config\
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
        "

RDEPENDS_${PN} += " \
        sdbusplus \
        "

SYSTEMD_SERVICE_${PN} += "obmc-read-eeprom@.service"

S = "${WORKDIR}/git"

HOSTIPMI_PROVIDER_LIBRARY += "libstrgfnhandler.so"

FILES_${PN}_append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV} ${libdir}/ipmid-providers/*.la"

# TODO: Fix the the ipmi-fru-parser code generator to handle split
# host firmware / inventory YAML and replace the OECONF below with:
#
# EXTRA_OECONF += "INVENTORY_YAML=${inventory_datadir}/config.yaml"
# EXTRA_OECONF += "HOSTFW_YAML=${hostfw_datadir}/config.yaml"
#
# For now the generator requires them to already be combined so we have:
EXTRA_OECONF = " \
             YAML_GEN=${STAGING_DIR_NATIVE}${config_datadir}/config.yaml \
             PROP_YAML=${STAGING_DIR_NATIVE}${properties_datadir}/extra-properties.yaml \
             "
