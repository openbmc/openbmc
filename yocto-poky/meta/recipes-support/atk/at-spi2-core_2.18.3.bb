SUMMARY = "Assistive Technology Service Provider Interface (dbus core)"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=e9f288ba982d60518f375b5898283886"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/${BPN}/${MAJ_VER}/${BPN}-${PV}.tar.xz \
           file://0001-nls.m4-Take-it-from-gettext-0.15.patch \
          "

SRC_URI[md5sum] = "fc18801e56f6ce6914126f837d42f556"
SRC_URI[sha256sum] = "ada26add94155f97d0f601a20cb7a0e3fd3ba1588c3520b7288316494027d629"

DEPENDS = "dbus glib-2.0 virtual/libx11 libxi libxtst intltool-native gettext-native"

inherit autotools gtk-doc pkgconfig distro_features_check upstream-version-is-even gobject-introspection
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECONF = "--disable-xevie --with-dbus-daemondir=${bindir}"

FILES_${PN} += "${datadir}/dbus-1/services/*.service \
                ${datadir}/dbus-1/accessibility-services/*.service"
