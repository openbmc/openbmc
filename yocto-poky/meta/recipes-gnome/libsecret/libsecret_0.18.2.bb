SUMMARY = "libsecret is a library for storing and retrieving passwords and other secrets"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=23c2a5e0106b99d75238986559bb5fc6"

inherit gnomebase gtk-doc

DEPENDS = "glib-2.0 libgcrypt gettext-native"

EXTRA_OECONF += "--disable-manpages"

SRC_URI[archive.md5sum] = "23cdf8267d11a26f88f0dbec1e2022ad"
SRC_URI[archive.sha256sum] = "12fd288b012e1b2b1b54d586cd4c6507885715534644b4534b7ef7d7079ba443"

# http://errors.yoctoproject.org/Errors/Details/20228/
ARM_INSTRUCTION_SET = "arm"
