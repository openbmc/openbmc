SUMMARY = "LibGTop2"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://copyright.txt;md5=dbc839bf158d19a20e661db14db7a58c"

inherit gnomebase lib_package gtk-doc distro_features_check gobject-introspection
# depends on libxau
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "ee29a9ef60659ebf4b075ac281f71cb2"
SRC_URI[archive.sha256sum] = "463bcbe5737b1b93f3345ee34abf601e8eb864f507c49ff1921c2737abafc1e5"

DEPENDS = "glib-2.0 intltool-native libxau"
