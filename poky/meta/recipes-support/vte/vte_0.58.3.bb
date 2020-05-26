SUMMARY = "Virtual terminal emulator GTK+ widget library"
BUGTRACKER = "https://bugzilla.gnome.org/buglist.cgi?product=vte"
LICENSE = "GPLv3 & LGPLv3+ & LGPLv2.1+"
LICENSE_libvte = "LGPLv3+"

LIC_FILES_CHKSUM = " \
    file://COPYING.GPL3;md5=2f31b266d3440dd7ee50f92cf67d8e6c \
    file://COPYING.LGPL2;md5=4fbd65380cdd255951079008b364516c \
    file://COPYING.LGPL3;md5=b52f2d57d10c4f7ee67a7eb9615d5d24 \
"

DEPENDS = "glib-2.0 gtk+3 libpcre2 libxml2-native gperf-native"

GNOMEBASEBUILDCLASS = "meson"
GIR_MESON_OPTION = 'gir'

inherit gnomebase gtk-doc features_check upstream-version-is-even gobject-introspection

# vapigen.m4 is required when vala is not present (but the one from vala should be used normally)
SRC_URI += "file://0001-app.cc-use-old-school-asignment-to-avoid-gcc-4.8-err.patch \
            file://0002-Add-W_EXITCODE-macro-for-non-glibc-systems.patch \
           "
SRC_URI[archive.md5sum] = "f59eb0784a361c0939c03d4566255916"
SRC_URI[archive.sha256sum] = "22dcb54ac2ad1a56ab0a745e16ccfeb383f0b5860b5bfa1784561216f98d4975"

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

# Instead of "inherit vala" we do the relevant settings here so we can
# set DEPENDS based on PACKAGECONFIG.

# Our patched version of Vala looks in STAGING_DATADIR for .vapi files
export STAGING_DATADIR
# Upstream Vala >= 0.11 looks in XDG_DATA_DIRS for .vapi files
export XDG_DATA_DIRS = "${STAGING_DATADIR}"

# Help g-ir-scanner find the .so for linking
do_compile_prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/src/.libs"
}

# Package additional files
FILES_${PN}-dev += "${datadir}/vala/vapi/*"

PACKAGECONFIG ??= "gnutls"
PACKAGECONFIG[vala] = "-Dvapi=true,-Dvapi=false,vala-native vala"
PACKAGECONFIG[gnutls] = "-Dgnutls=true,-Dgnutls=false,gnutls"
# vala requires gir
PACKAGECONFIG_remove_class-native = "vala"

CFLAGS += "-D_GNU_SOURCE"

PACKAGES =+ "libvte ${PN}-prompt"
FILES_libvte = "${libdir}/*.so.* ${libdir}/girepository-1.0/*"
FILES_${PN}-prompt = "${sysconfdir}/profile.d"

BBCLASSEXTEND = "native nativesdk"
