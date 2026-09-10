require xorg-doc-common.inc

PE = "1"

SUMMARY = "SGML entities and XML/CSS stylesheets used in X.Org docs"

LIC_FILES_CHKSUM = "file://COPYING;md5=4cd7c44828c87cd91077a7d85098f117"

SRC_URI_EXT = "xz"

SRC_URI[sha256sum] = "0a5d54c0706b4e89d5acd4d455db3745ab4ad26be627cce015b90ad403b56d6f"

FILES:${PN} += "${datadir}/sgml/X11"
