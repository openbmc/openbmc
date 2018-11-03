SUMMARY = "Phosphor DBus Monitor Configuration"
DESCRIPTION = "Meta-recipe, pulling in native recipes that wish to add \
configuration files to the /usr/share/phosphor-dbus-monitor filesystem."
HOMEPAGE = "http://github.com/openbmc/phosphor-dbus-monitor"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-dbus-monitor

PHOSPHOR_DBUS_MONITOR_CONFIGS ??= ""

# To add additional config files, create a recipe in your layer,
# and add it to PHOSPHOR_DBUS_MONITOR_CONFIGS with a bbappend to this recipe.

DEPENDS += "${PHOSPHOR_DBUS_MONITOR_CONFIGS}"

do_install() {
    mkdir -p ${D}${config_dir}
}
