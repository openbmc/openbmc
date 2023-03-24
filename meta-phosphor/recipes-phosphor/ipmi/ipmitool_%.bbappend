LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=9aa91e13d644326bf281924212862184"
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
DEPENDS += "systemd"
SRCREV = "c3939dac2c060651361fc71516806f9ab8c38901"
PV = "1.8.18+git${SRCPV}"

SRC_URI = "git://github.com/ipmitool/ipmitool.git;protocol=https;branch=master"
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

S = "${WORKDIR}/git"

EXTRA_OECONF:append = " --disable-ipmishell --enable-intf-dbus DEFAULT_INTF=dbus "

do_compile:prepend() {
    # copy the SRC_URI version of enterprise-numbers
    # to the build dir to prevent a fetch
    mkdir -p "${WORKDIR}/build"
    cp "${WORKDIR}/enterprise-numbers" "${WORKDIR}/build/enterprise-numbers"
}

# make sure that the enterprise-numbers file gets installed in the root FS
FILES:${PN} += "${datadir}/misc/enterprise-numbers"
