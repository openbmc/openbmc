FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

DEPENDS += "systemd"

SRC_URI += "file://0001-plugins-Add-a-backend-for-the-OpenBMC-dbus-interface.patch"
