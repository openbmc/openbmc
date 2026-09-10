SUMMARY = "malcontent implements support for restricting the type of content."
HOMEPAGE = "https://gitlab.freedesktop.org/pwithnall/malcontent"

require malcontent.inc

DEPENDS = " \
	accountsservice \
	glib-2.0 \
	glib-testing \
	dbus \
	itstool-native \
	libpam \
	polkit \
	gi-docgen-native \
	libsoup \
"

GIR_MESON_OPTION = ""

inherit  meson pkgconfig gobject-introspection gettext features_check useradd

REQUIRED_DISTRO_FEATURES = "pam polkit gobject-introspection"

PACKAGECONFIG ?= "ui"
PACKAGECONFIG[ui] = ",,,malcontent-ui"

EXTRA_OEMESON = "-Dui=disabled -Dpamlibdir=${base_libdir}/security"

FILES:${PN} += " \
    ${nonarch_libdir}/sysusers.d \
    ${systemd_system_unitdir} \
    ${base_libdir}/security/pam_malcontent.so \
    ${datadir}/accountsservice \
    ${datadir}/help \
    ${datadir}/dbus-1 \
    ${datadir}/polkit-1 \
"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --user-group --shell /bin/false --home-dir ${localstatedir}  malcontent-timer-ext-agent;malcontent-timerd;malcontent-webd"

