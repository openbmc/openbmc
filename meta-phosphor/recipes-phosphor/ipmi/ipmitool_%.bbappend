FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

DEPENDS += "systemd"

SRC_URI = "git://github.com/ipmitool/ipmitool.git;protocol=https"
SRCREV = "c3939dac2c060651361fc71516806f9ab8c38901"

# TODO: when a new company joins the OpenBMC project by signing
#       a CLA, if they have an enterprise number on file with the
#       IANA, the versioned file, $PWD/ipmitool/enterprise-numbers
#       needs to be updated to add their entry. The canonical
#       version of the file is locatede here:
#       https://www.iana.org/assignments/enterprise-numbers
#
#       This file is manually downloaded so it can be versioned
#       instead of having the makefile download it during do_compile
SRC_URI += " \
    file://enterprise-numbers \
    "

# make sure that the enterprise-numbers file gets installed in the root FS
FILES_${PN} += "/usr/share/misc/enterprise-numbers"
do_compile_prepend() {
    # copy the SRC_URI version of enterprise-numbers
    # to the build dir to prevent a fetch
    mkdir -p "${WORKDIR}/build"
    cp "${WORKDIR}/enterprise-numbers" "${WORKDIR}/build/enterprise-numbers"
}

S = "${WORKDIR}/git"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=9aa91e13d644326bf281924212862184"

EXTRA_OECONF_append = " --disable-ipmishell --enable-intf-dbus DEFAULT_INTF=dbus "

PV = "1.8.18+git${SRCPV}"
