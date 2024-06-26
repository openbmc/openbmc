SUMMARY = "Phosphor IPMI Inventory Plugin"
DESCRIPTION = "A Phosphor IPMI plugin that updates inventory."
DEPENDS += " \
        virtual/phosphor-ipmi-fru-inventory \
        virtual/phosphor-ipmi-fru-properties \
        sdbusplus \
        ${PYTHON_PN}-mako-native \
        ${PYTHON_PN}-pyyaml-native \
        phosphor-ipmi-host \
        phosphor-logging \
        cli11 \
        "
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI += "file://of-name-to-eeprom.sh"

SYSTEMD_SERVICE:${PN} += "obmc-read-eeprom@.service"
S = "${WORKDIR}/git"

inherit meson pkgconfig
inherit obmc-phosphor-systemd
inherit obmc-phosphor-ipmiprovider-symlink
inherit phosphor-ipmi-fru
inherit python3native

IPMI_FRU_YAML ?= "${STAGING_DIR_NATIVE}${config_datadir}/config.yaml"
IPMI_FRU_PROP_YAML ?= "${STAGING_DIR_NATIVE}${properties_datadir}/extra-properties.yaml"


EXTRA_OEMESON = " \
        -Dfru_yaml=${IPMI_FRU_YAML} \
        -Dproperties_yaml=${IPMI_FRU_PROP_YAML} \
        "

do_install:append() {
        install -d ${D}${bindir}
        install -m 0755 ${UNPACKDIR}/of-name-to-eeprom.sh ${D}${bindir}
}

RDEPENDS:${PN} += "bash"

FILES:${PN} += "${bindir}/of-name-to-eeprom.sh"
FILES:${PN}:append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES:${PN}-dev:append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV} ${libdir}/ipmid-providers/*.la"

require ${BPN}.inc

HOSTIPMI_PROVIDER_LIBRARY += "libstrgfnhandler.so"
