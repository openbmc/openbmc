FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

EXTRA_OECONF_append_yosemitev2 = " --enable-configure-dbus=yes"
