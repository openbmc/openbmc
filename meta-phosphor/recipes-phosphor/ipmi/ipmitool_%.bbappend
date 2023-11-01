LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=9aa91e13d644326bf281924212862184"
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
DEPENDS += "systemd"

# Temporary patch for https://codeberg.org/IPMITool/ipmitool/pulls/1
SRC_URI += " \
    file://0001-Fru-Fix-edit-field-not-checking-area-existence.patch \
"

# TODO: when a new company joins the OpenBMC project by signing
#       a CLA, if they have an enterprise number on file with the
#       IANA, the versioned file, $PWD/ipmitool/iana-enterprise-numbers
#       needs to be updated to add their entry. The canonical
#       version of the file is locatede here:
#       https://www.iana.org/assignments/enterprise-numbers
#
#       This file is manually downloaded so it can be versioned
#       instead of having the makefile download it during do_compile
SRC_URI += " \
    file://iana-enterprise-numbers \
    "

S = "${WORKDIR}/git"

EXTRA_OECONF:append = " --disable-ipmishell --enable-intf-dbus DEFAULT_INTF=dbus "
