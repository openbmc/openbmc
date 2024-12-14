SUMMARY = "Virtual terminal emulator GTK+ widget library"
DESCRIPTION = "VTE provides a virtual terminal widget for GTK applications."
HOMEPAGE = "https://wiki.gnome.org/Apps/Terminal/VTE"
BUGTRACKER = "https://bugzilla.gnome.org/buglist.cgi?product=vte"
LICENSE = "GPL-3.0-only & LGPL-3.0-or-later & MIT"
LICENSE:libvte = "LGPL-3.0-or-later"

LIC_FILES_CHKSUM = " \
    file://COPYING.GPL3;md5=cc702cf3444d1f19680c794cc61948f9 \
    file://COPYING.LGPL3;md5=b52f2d57d10c4f7ee67a7eb9615d5d24 \
    file://COPYING.XTERM;md5=d7fc3a23c16c039afafe2e042030f057 \
"

DEPENDS = "glib-2.0 glib-2.0-native gtk+3 libpcre2 libxml2-native gperf-native icu lz4"

GIR_MESON_OPTION = 'gir'
GIDOCGEN_MESON_OPTION = "docs"
inherit gnomebase gi-docgen features_check upstream-version-is-even gobject-introspection systemd vala

SRC_URI += "file://0001-Add-W_EXITCODE-macro-for-non-glibc-systems.patch \
           file://0002-lib-Typo-fix.patch \
           file://0004-fast_float-Add-single-header-library-for-from_char-i.patch \
           file://0005-color-parser-Use-fast_float-implementation-for-from_.patch \
           "
SRC_URI[archive.sha256sum] = "35d7bcde07356846b4a12881c8e016705b70a9004a9082285eee5834ccc49890"

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

EXTRA_OEMESON += "${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-Dvapi=true', '-Dvapi=false', d)}"
EXTRA_OEMESON:append = " ${@bb.utils.contains('GI_DATA_ENABLED', 'False', '-Ddocs=false', '', d)}"

PACKAGECONFIG ??= " \
	gnutls \
	${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'gtk4', '', d)} \
"
PACKAGECONFIG[fribidi] = "-Dfribidi=true,-Dfribidi=false,fribidi"
PACKAGECONFIG[gtk4] = "-Dgtk4=true,-Dgtk4=false,gtk4"
PACKAGECONFIG[gnutls] = "-Dgnutls=true,-Dgnutls=false,gnutls"
PACKAGECONFIG[systemd] = "-D_systemd=true,-D_systemd=false,systemd"

PACKAGES =+ "libvte-gtk4 ${PN}-gtk4 ${PN}-gtk4-dev libvte ${PN}-prompt"
FILES:libvte-gtk4 = "${libdir}/lib*gtk4.so.* ${libdir}/girepository-1.0/Vte-3.91.typelib"
FILES:${PN}-gtk4 ="${bindir}/vte-2.91-gtk4"
FILES:${PN}-gtk4-dev = "${libdir}/lib*gtk4.so \
                        ${libdir}/pkgconfig/vte-2.91-gtk4.pc \
                        ${datadir}/gir-1.0/Vte-3.91.gir \
                        ${datadir}/vala/vapi/vte-2.91-gtk4.deps \
                        ${datadir}/vala/vapi/vte-2.91-gtk4.vapi \
                        ${includedir}/vte-2.91-gtk4 \
                        "
FILES:${PN} +="${systemd_user_unitdir}"
FILES:libvte = "${libdir}/*.so.* ${libdir}/girepository-1.0/*"
FILES:${PN}-prompt = " \
    ${sysconfdir}/profile.d \
    ${libexecdir}/vte-urlencode-cwd \
"

FILES:${PN}-dev += "${datadir}/glade/"
