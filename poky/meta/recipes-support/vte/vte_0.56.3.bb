SUMMARY = "Virtual terminal emulator GTK+ widget library"
BUGTRACKER = "https://bugzilla.gnome.org/buglist.cgi?product=vte"
LICENSE = "GPLv3 & LGPLv3+ & LGPLv2.1+"
LICENSE_libvte = "LGPLv3+"

LIC_FILES_CHKSUM = " \
    file://COPYING.GPL3;md5=2f31b266d3440dd7ee50f92cf67d8e6c \
    file://COPYING.LGPL2;md5=4fbd65380cdd255951079008b364516c \
    file://COPYING.LGPL3;md5=b52f2d57d10c4f7ee67a7eb9615d5d24 \
"

DEPENDS = "glib-2.0 gtk+3 libpcre2 intltool-native libxml2-native gperf-native"

inherit gnomebase gtk-doc distro_features_check upstream-version-is-even gobject-introspection

# vapigen.m4 is required when vala is not present (but the one from vala should be used normally)
SRC_URI += "file://0001-Don-t-enable-stack-protection-by-default.patch \
           ${@bb.utils.contains('PACKAGECONFIG', 'vala', '', 'file://0001-Add-m4-vapigen.m4.patch', d) } \
           file://0001-app.cc-use-old-school-asignment-to-avoid-gcc-4.8-err.patch \
           file://0001-Add-W_EXITCODE-macro-for-non-glibc-systems.patch \
           "
SRC_URI[archive.md5sum] = "adf341807861a5dad9f98e5c701c0769"
SRC_URI[archive.sha256sum] = "17a1d4bc8848f1d2acfa4c20aaa24b9bac49f057b8909c56d3dafec2e2332648"

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
PACKAGECONFIG[vala] = "--enable-vala,--disable-vala,vala-native vala"
PACKAGECONFIG[gnutls] = "--with-gnutls,--without-gnutls,gnutls"

CFLAGS += "-D_GNU_SOURCE"

# libtool adds "-nostdlib" when g++ is used. This breaks PIE builds.
# Use libtool-cross (which has a hack to prevent that) instead.
EXTRA_OEMAKE_class-target = "LIBTOOL=${STAGING_BINDIR_CROSS}/${HOST_SYS}-libtool"

PACKAGES =+ "libvte ${PN}-prompt"
FILES_libvte = "${libdir}/*.so.* ${libdir}/girepository-1.0/*"
FILES_${PN}-prompt = "${sysconfdir}/profile.d"

BBCLASSEXTEND = "native nativesdk"
