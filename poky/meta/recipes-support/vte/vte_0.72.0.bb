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

DEPENDS = "glib-2.0 glib-2.0-native gtk+3 libpcre2 libxml2-native gperf-native icu"

GNOMEBASEBUILDCLASS = "meson"
GIR_MESON_OPTION = 'gir'
GIDOCGEN_MESON_OPTION = "docs"

inherit gnomebase gi-docgen features_check upstream-version-is-even gobject-introspection

# vapigen.m4 is required when vala is not present (but the one from vala should be used normally)
SRC_URI += "file://0001-Add-W_EXITCODE-macro-for-non-glibc-systems.patch"
SRC_URI[archive.sha256sum] = "40fe914d6c70db34176c922725b6c6ea15d5f3cb2a9b44c57e200a5f950a6736"

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

# Help g-ir-scanner find the .so for linking
do_compile:prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/src/.libs"
}

# Package additional files
FILES:${PN}-dev += "${datadir}/vala/vapi/*"

PACKAGECONFIG ??= "gnutls"
PACKAGECONFIG[vala] = "-Dvapi=true,-Dvapi=false,vala-native vala"
PACKAGECONFIG[gnutls] = "-Dgnutls=true,-Dgnutls=false,gnutls"
PACKAGECONFIG[systemd] = "-D_systemd=true,-D_systemd=false,systemd"
# vala requires gir
PACKAGECONFIG:remove:class-native = "vala"

CFLAGS += "-D_GNU_SOURCE"

PACKAGES =+ "libvte ${PN}-prompt"
FILES:libvte = "${libdir}/*.so.* ${libdir}/girepository-1.0/*"
FILES:${PN}-prompt = " \
    ${sysconfdir}/profile.d \
    ${libexecdir}/vte-urlencode-cwd \
"

FILES:${PN}-dev += "${datadir}/glade/"

BBCLASSEXTEND = "native nativesdk"
