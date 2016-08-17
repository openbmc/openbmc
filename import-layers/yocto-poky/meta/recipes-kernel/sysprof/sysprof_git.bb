SUMMARY = "System-wide Performance Profiler for Linux"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "glib-2.0"

SRCREV = "cd44ee6644c3641507fb53b8a2a69137f2971219"
PV = "1.2.0+git${SRCPV}"

SRC_URI = "git://git.gnome.org/sysprof \
           file://define-NT_GNU_BUILD_ID.patch \
           file://gui-argument.patch \
          "

SRC_URI_append_arm  = " file://rmb-arm.patch"
SRC_URI_append_armeb  = " file://rmb-arm.patch"
SRC_URI_append_mips = " file://rmb-mips.patch"
SRC_URI_append_mips64 = " file://rmb-mips.patch"
SRC_URI_append_mips64n32 = " file://rmb-mips.patch"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

PACKAGECONFIG ?= "${@bb.utils.contains_any('DISTRO_FEATURES', '${GTK2DISTROFEATURES}', 'gui', '', d)}"
PACKAGECONFIG[gui] = "--enable-gui,--disable-gui,gtk+ gdk-pixbuf pango libglade"

# We do not yet work for aarch64.
#
COMPATIBLE_HOST = "^(?!aarch64).*"

