SUMMARY = "Small gobject library for playing system sounds"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=447b837ae57f08b7060593ac6256163f"

DEPENDS = " \
    glib-2.0 \
    libcanberra \
"

inherit gnomebase gettext gobject-introspection vala

SRC_URI[archive.md5sum] = "c26fd21c21b9ef6533a202a73fab21db"
SRC_URI[archive.sha256sum] = "bba8ff30eea815037e53bee727bbd5f0b6a2e74d452a7711b819a7c444e78e53"
