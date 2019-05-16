require pseudo.inc

SRC_URI = "git://git.yoctoproject.org/pseudo \
           file://0001-configure-Prune-PIE-flags.patch \
           file://fallback-passwd \
           file://fallback-group \
           file://moreretries.patch \
           file://toomanyfiles.patch \
           "

SRCREV = "3fa7c853e0bcd6fe23f7524c2a3c9e3af90901c3"
S = "${WORKDIR}/git"
PV = "1.9.0+git${SRCPV}"

