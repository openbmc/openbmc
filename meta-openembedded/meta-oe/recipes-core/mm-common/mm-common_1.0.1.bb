SUMMARY = "Common GNOME build files for C++ bindings"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase

SRC_URI[archive.md5sum] = "5b5a589f648bd83bfa6291081ebf5864"
SRC_URI[archive.sha256sum] = "28a2d775afbf05c5c957b24b220ed6e9b2023179b98ecde471d89e9754ea5ac9"
SRC_URI += "file://0001-meson.build-do-not-ask-for-python-installation-versi.patch"

BBCLASSEXTEND = "native"
