FILESEXTRAPATHS:prepend:evb-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:evb-npcm845 = " file://0001-update-NMISource-interface-from-intel-dbus-interface.patch"
