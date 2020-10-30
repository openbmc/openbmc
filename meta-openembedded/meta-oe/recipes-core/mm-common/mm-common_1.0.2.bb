SUMMARY = "Common GNOME build files for C++ bindings"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase

SRC_URI[archive.md5sum] = "440133a2676275ae26770b3651f89827"
SRC_URI[archive.sha256sum] = "a2a99f3fa943cf662f189163ed39a2cfc19a428d906dd4f92b387d3659d1641d"
SRC_URI += "file://0001-meson.build-do-not-ask-for-python-installation-versi.patch"

BBCLASSEXTEND = "native"
