SUMMARY = "Phosphor DBus Monitor Configuration"
DESCRIPTION = "Meta-recipe, pulling in native recipes that wish to add \
configuration files to the /usr/share/phosphor-dbus-monitor filesystem."
HOMEPAGE = "http://github.com/openbmc/phosphor-dbus-monitor"
PR = "r1"

inherit obmc-phosphor-license
inherit native
inherit phosphor-dbus-monitor

PHOSPHOR_DBUS_MONITOR_CONFIGS ??= ""

SRC_URI += "file://empty.yaml"

# To add additional config files, create a recipe in your layer,
# and add it to PHOSPHOR_DBUS_MONITOR_CONFIGS with a bbappend to this recipe.

DEPENDS += "${PHOSPHOR_DBUS_MONITOR_CONFIGS}"

do_install() {
        install -D ${WORKDIR}/empty.yaml ${D}${config_dir}/empty.yaml
}
