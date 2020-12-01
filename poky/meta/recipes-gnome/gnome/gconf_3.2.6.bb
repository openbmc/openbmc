SUMMARY = "GNOME configuration system"
DESCRIPTION = "GConf is a system for storing application preferences. \
It is intended for user preferences; not configuration of something like \
Apache, or arbitrary data storage."
SECTION = "x11/gnome"
HOMEPAGE = "https://gitlab.gnome.org/Archive/gconf"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=55ca817ccb7d5b5b66355690e9abc605"

DEPENDS = "glib-2.0 dbus dbus-glib libxml2 intltool-native"

inherit gnomebase gtk-doc gettext gobject-introspection gio-module-cache

SRC_URI = "${GNOME_MIRROR}/GConf/${@gnome_verdir("${PV}")}/GConf-${PV}.tar.xz;name=archive \
           file://remove_plus_from_invalid_characters_list.patch \
           file://unable-connect-dbus.patch \
           file://create_config_directory.patch \
"

SRC_URI[archive.md5sum] = "2b16996d0e4b112856ee5c59130e822c"
SRC_URI[archive.sha256sum] = "1912b91803ab09a5eed34d364bf09fe3a2a9c96751fde03a4e0cfa51a04d784c"

S = "${WORKDIR}/GConf-${PV}"

EXTRA_OECONF = "--enable-shared --disable-static \
                --disable-orbit --with-openldap=no --disable-gtk"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)}"
# We really don't want Polkit for native
PACKAGECONFIG_class-native = ""

PACKAGECONFIG[polkit] = "--enable-defaults-service,--disable-defaults-service,polkit"
PACKAGECONFIG[debug] = "--enable-debug=yes, --enable-debug=minimum"

do_install_append() {
	# this directory need to be created to avoid an Error 256 at gdm launch
	install -d ${D}${sysconfdir}/gconf/gconf.xml.system

	# this stuff is unusable
	rm -f ${D}${libdir}/GConf/*/*.*a
	rm -f ${D}${libdir}/gio/*/*.*a
}

do_install_append_class-native() {
	create_wrapper ${D}/${bindir}/gconftool-2 \
		GCONF_BACKEND_DIR=${STAGING_LIBDIR_NATIVE}/GConf/2
}

FILES_${PN} += "${libdir}/GConf/* \
                ${libdir}/gio/*/*.so \
                ${datadir}/polkit* \
                ${datadir}/dbus-1/services/*.service \
                ${datadir}/dbus-1/system-services/*.service \
               "
FILES_${PN}-dev += "${datadir}/sgml/gconf/gconf-1.0.dtd"

BBCLASSEXTEND = "native"
