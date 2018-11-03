SUMMARY = "Phosphor Mapper Configuration"
DESCRIPTION = "Meta-recipe, pulling in native recipes that wish to add \
configuration files to the native /usr/share/phosphor-mapper filesystem."
HOMEPAGE = "http://github.com/openbmc/phosphor-objmgr"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-mapper

PHOSPHOR_MAPPER_CONFIGS = " \
        phosphor-dbus-interfaces-mapper-config-native \
        phosphor-legacy-namespace-mapper-config-native \
        "

DEPENDS += "${PHOSPHOR_MAPPER_CONFIGS}"

# To add namespaces and blacklists to the mapper configuration,
# create a native recipe in your layer, and add it to
# PHOSPHOR_MAPPER_CONFIGS with a bbappend to this recipe.
# Recipes should set one of the variables below.
# Consult phosphor-mapper.bbclass for additional information.

# Add services to be monitored.
PHOSPHOR_MAPPER_SERVICE = ""

# Add interfaces to be monitored.
PHOSPHOR_MAPPER_INTERFACE = ""

# Blacklist services from being monitored.
PHOSPHOR_MAPPER_SERVICE_BLACKLIST = ""

do_install() {
        install -d ${D}/${namespace_dir}
        install -d ${D}/${interface_dir}
        install -d ${D}/${serviceblacklist_dir}
}
