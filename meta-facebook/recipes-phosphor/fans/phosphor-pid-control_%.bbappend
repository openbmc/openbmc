FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

EXTRA_OECONF_append_tiogapass = " --enable-configure-dbus=yes"
