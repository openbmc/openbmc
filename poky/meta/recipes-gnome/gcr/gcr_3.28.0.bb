SUMMARY = "A library for bits of crypto UI and parsing etc"
HOMEPAGE = "http://www.gnome.org/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=55ca817ccb7d5b5b66355690e9abc605"

DEPENDS = "intltool-native gtk+3 p11-kit glib-2.0 libgcrypt \
           ${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'libxslt-native', '', d)}"

inherit gnomebase gtk-icon-cache gtk-doc distro_features_check upstream-version-is-even vala gobject-introspection
# depends on gtk+3, but also x11 through gtk+-x11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "5321319307dad34dca2fd52e7c9c01ab"
SRC_URI[archive.sha256sum] = "15e175d1da7ec486d59749ba34906241c442898118ce224a7b70bf2e849faf0b"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/gcr-3 \
"

# http://errors.yoctoproject.org/Errors/Details/20229/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"
ARM_INSTRUCTION_SET_armv6 = "arm"

# These files may be out of date or missing our fixes
# libgcrypt.m4 in particular is calling into libgcrypt-config
do_configure_prepend() {
    rm -f ${S}/build/m4/*
}
