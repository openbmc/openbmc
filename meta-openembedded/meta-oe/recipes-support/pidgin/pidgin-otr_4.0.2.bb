SUMMARY = "(OTR) Messaging allows you to have private conversations over instant messaging"
HOMEPAGE = "https://otr.cypherpunks.ca/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=92fe174bad6da3763f6e9e9eaff6df24"
DEPENDS = "libgcrypt libotr pidgin gtk+ intltool-native glib-2.0 glib-2.0-native"
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "https://otr.cypherpunks.ca/${BP}.tar.gz \
"
SRC_URI[md5sum] = "7ef14e1334a4bc80e5d530f9a3cfc626"
SRC_URI[sha256sum] = "f4b59eef4a94b1d29dbe0c106dd00cdc630e47f18619fc754e5afbf5724ebac4"

FILES:${PN} = "${libdir}/pidgin/*"

inherit autotools pkgconfig features_check
