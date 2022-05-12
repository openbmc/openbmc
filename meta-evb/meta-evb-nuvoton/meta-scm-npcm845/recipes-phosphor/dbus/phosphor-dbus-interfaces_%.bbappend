FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:scm-npcm845 = " file://0001-update-NMISource-interface-from-intel-dbus-interface.patch"
