SUMMARY = "Phosphor DBus Monitor Configuration"
DESCRIPTION = "Meta-recipe, pulling in non-native recipes that wish to add \
configuration files to the /usr/share/phosphor-dbus-monitor filesystem."
HOMEPAGE = "http://github.com/openbmc/phosphor-dbus-monitor"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit phosphor-dbus-monitor

PHOSPHOR_DBUS_MONITOR_CONFIGS ??= ""

# To add additional config files, create a recipe in your layer,
# and add it to PHOSPHOR_DBUS_MONITOR_CONFIGS with a bbappend to this recipe.

DEPENDS += "${PHOSPHOR_DBUS_MONITOR_CONFIGS}"

do_install() {
    mkdir -p ${D}${config_dir}
}

FILES_${PN} += "${config_dir}"
