SUMMARY     = "I2C debug tool"
DESCRIPTION = "iicmaster provides a debug interface for remote, FSI-based I2C engines"
LICENSE     = "GPLv3"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=84dcc94da3adb52b53ae4fa38fe49e5d"

SRC_URI += "git://github.com/eddiejames/iicmaster.git"

SRCREV = "b71dbf99af495a631dc5f3564296903667bc516c"
PV = "git${SRCREV}"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 iicmaster ${D}${bindir}
}
