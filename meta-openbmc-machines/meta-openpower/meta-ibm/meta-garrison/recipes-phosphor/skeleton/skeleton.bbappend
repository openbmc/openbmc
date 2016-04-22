FILESEXTRAPATHS_append := "${THISDIR}/${PN}:"
SRC_URI += "file://garrison.patch"
SRC_URI += "file://poweron.patch"
SRC_URI += "file://occ-path.patch"
