SUMMARY = "Common GNOME build files for C++ bindings"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase

SRC_URI[archive.sha256sum] = "e81596625899aacf1d0bf27ccc2fcc7f373405ec48735ca1c7273c0fbcdc1ef5"
SRC_URI += "file://0001-meson.build-do-not-ask-for-python-installation-versi.patch"

BBCLASSEXTEND = "native"
