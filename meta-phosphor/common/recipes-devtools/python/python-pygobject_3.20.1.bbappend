FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
RDEPENDS_${PN}_class-native = ""
SRC_URI += "file://remove_GNOME_compiler_warning.patch"
