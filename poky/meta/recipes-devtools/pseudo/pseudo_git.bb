require pseudo.inc

SRC_URI = "git://git.yoctoproject.org/pseudo \
           file://0001-configure-Prune-PIE-flags.patch \
           file://fallback-passwd \
           file://fallback-group \
           file://moreretries.patch \
           file://toomanyfiles.patch \
           "

SRCREV = "6294b344e5140f5467e6860f45a174440015304e"
S = "${WORKDIR}/git"
PV = "1.9.0+git${SRCPV}"

