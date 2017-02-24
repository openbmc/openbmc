SUMMARY = "A library for bits of crypto UI and parsing etc"
HOMEPAGE = "http://www.gnome.org/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=55ca817ccb7d5b5b66355690e9abc605"

DEPENDS = "intltool-native gtk+3 p11-kit glib-2.0 libgcrypt"

inherit autotools gnomebase gtk-icon-cache gtk-doc distro_features_check upstream-version-is-even vala gobject-introspection
# depends on gtk+3, but also x11 through gtk+-x11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "4314bf89eac293dd0a9d806593ff1b35"
SRC_URI[archive.sha256sum] = "90572c626d8a708225560c42b4421f7941315247fa1679d4ef569bde7f4bb379"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/gcr-3 \
"

# http://errors.yoctoproject.org/Errors/Details/20229/
ARM_INSTRUCTION_SET = "arm"

# on x86-64 the introspection binary goes into 
# an infinite loop under qemu during compilation, 
# printing the following:
# 
# gcrypt-Message: select() error: Bad address
#
# gcrypt-Message: select() error: Bad address
#
# gcrypt-Message: select() error: Bad address
#
# This will be investigated later.
EXTRA_OECONF_append_x86-64 = " --disable-introspection --disable-gtk-doc"

# Gcr-3.broken: poky/build-mips64/tmp/work/mips64-poky-linux/libgpg-error/1.19-r0/libgpg-error-1.19/src/posix-lock.c:119: get_lock_object: Assertion `!"sizeof lock obj"' failed.
# qemu: uncaught target signal 6 (Aborted) - core dumped
EXTRA_OECONF_append_mips64 = " --disable-introspection --disable-gtk-doc"
