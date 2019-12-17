require ${BPN}.inc

inherit meson gobject-introspection gsettings gettext bash-completion systemd features_check useradd

# polkit and gobject-introspection are mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "polkit gobject-introspection-data"
UNKNOWN_CONFIGURE_WHITELIST_append = " introspection"

DEPENDS += " \
    ${BPN}-native \
    glib-2.0 \
    lcms \
    sqlite3 \
    libgusb \
    libgudev \
    polkit \
"

SRC_URI += " \
    file://0001-Run-native-cd_idt8-cd_create_profile.patch \
"

EXTRA_OEMESON = " \
    -Dman=false \
    -Ddocs=false \
"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "-Dsystemd=true, -Dsystemd=false, systemd"

SYSTEMD_SERVICE_${PN} = "colord.service"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/polkit-1 \
    ${datadir}/glib-2.0 \
    ${datadir}/color \
    ${systemd_user_unitdir} \
    ${libdir}/tmpfiles.d \
    ${libdir}/colord-plugins \
    ${libdir}/colord-sensors \
"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --user-group -d /var/lib/colord -s /bin/false colord"
