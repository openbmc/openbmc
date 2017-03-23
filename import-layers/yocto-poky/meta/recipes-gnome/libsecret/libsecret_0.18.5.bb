SUMMARY = "libsecret is a library for storing and retrieving passwords and other secrets"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=23c2a5e0106b99d75238986559bb5fc6"

inherit gnomebase gtk-doc vala gobject-introspection

DEPENDS += "glib-2.0 libgcrypt gettext-native intltool-native"

EXTRA_OECONF += "--disable-manpages"

SRC_URI[archive.md5sum] = "2d688825921313da8731313b0a7f394c"
SRC_URI[archive.sha256sum] = "9ce7bd8dd5831f2786c935d82638ac428fa085057cc6780aba0e39375887ccb3"

# http://errors.yoctoproject.org/Errors/Details/20228/
ARM_INSTRUCTION_SET = "arm"

# vapigen.m4 bundled with the tarball does not yet have our cross-compilation fixes
do_configure_prepend() {
    rm -f ${S}/build/m4/vapigen.m4
}

# Secret-1: poky/build-mips64/tmp/work/mips64-poky-linux/libgpg-error/1.19-r0/libgpg-error-1.19/src/posix-lock.c:119: get_lock_object: Assertion `!"sizeof lock obj"' failed.
# qemu: uncaught target signal 6 (Aborted) - core dumped
EXTRA_OECONF_append_mips64 = " --disable-introspection --disable-gtk-doc"

