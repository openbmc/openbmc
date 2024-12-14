require ${BPN}.inc

inherit meson gobject-introspection gsettings gtk-doc gettext bash-completion systemd features_check useradd pkgconfig

# polkit and gobject-introspection are mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "polkit gobject-introspection-data"
GIR_MESON_OPTION = ""

DEPENDS += " \
	${BPN}-native \
	dbus \
	glib-2.0 \
	lcms \
	libgudev \
	libgusb \
	polkit \
	sqlite3 \
"

RDEPENDS:${PN} += "hwdata"

SRC_URI += " \
	file://0001-Run-native-cd_idt8-cd_create_profile.patch \
	file://08a32b2379fb5582f4312e59bf51a2823df56276.patch \
"

EXTRA_OEMESON += " \
	-Dman=false \
	-Ddaemon_user=colord \
	-Dpnp_ids=${datadir}/hwdata/pnp.ids \
"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "-Dsystemd=true, -Dsystemd=false, systemd"

SYSTEMD_SERVICE:${PN} = "colord.service"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/polkit-1 \
    ${datadir}/glib-2.0 \
    ${datadir}/color \
    ${systemd_user_unitdir} \
    ${nonarch_libdir}/tmpfiles.d \
    ${libdir}/colord-plugins \
    ${libdir}/colord-sensors \
"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --user-group -s /bin/false colord"

