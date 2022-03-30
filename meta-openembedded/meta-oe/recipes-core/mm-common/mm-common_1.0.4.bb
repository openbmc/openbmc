SUMMARY = "Common GNOME build files for C++ bindings"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase

SRC_URI[archive.sha256sum] = "e954c09b4309a7ef93e13b69260acdc5738c907477eb381b78bb1e414ee6dbd8"
SRC_URI += "file://0001-meson.build-do-not-ask-for-python-installation-versi.patch"

BBCLASSEXTEND = "native"
