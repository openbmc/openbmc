SUMMARY = "Phosphor IPMI Inventory Plugin"
DESCRIPTION = "A Phosphor IPMI plugin that updates inventory."
PR = "r1"

inherit autotools pkgconfig
inherit obmc-phosphor-systemd
inherit phosphor-ipmi-fru
inherit pythonnative

require ${PN}.inc

DEPENDS += " \
        virtual/phosphor-ipmi-fru-hostfw-config\
        virtual/phosphor-ipmi-fru-inventory \
        systemd \
        python-mako-native \
        python-pyyaml-native \
        phosphor-ipmi-host \
        phosphor-mapper \
        autoconf-archive-native \
        "

RDEPENDS_${PN} += "libsystemd"

SYSTEMD_SERVICE_${PN} += "obmc-read-eeprom@.service"

S = "${WORKDIR}/git"

FILES_${PN}_append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/host-ipmid/lib*${SOLIBSDEV} ${libdir}/host-ipmid/*.la"

# TODO: Fix the the ipmi-fru-parser code generator to handle split
# host firmware / inventory YAML and replace the OECONF below with:
#
# EXTRA_OECONF += "INVENTORY_YAML=${inventory_datadir}/config.yaml"
# EXTRA_OECONF += "HOSTFW_YAML=${hostfw_datadir}/config.yaml"
#
# For now the generator requires them to already be combined so we have:
EXTRA_OECONF = "YAML_GEN=${config_datadir}/config.yaml"
