FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

DEPENDS += "systemd"

SRC_URI = "git://github.com/ipmitool/ipmitool.git;protocol=https"
SRCREV = "d818c2ff85c011be29c8d3047e516a5e032a1923"

# this patch has been submitted to ipmitool upstream and is in review
SRC_URI += " \
    file://0001-create_pen_list-only-print-if-values-are-set.patch \
    "

S = "${WORKDIR}/git"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=9aa91e13d644326bf281924212862184"

EXTRA_OECONF_append = " --disable-ipmishell --enable-intf-dbus DEFAULT_INTF=dbus "

PV = "1.8.18+git${SRCPV}"
