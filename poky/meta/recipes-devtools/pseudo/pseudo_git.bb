require pseudo.inc

SRC_URI = "git://git.yoctoproject.org/pseudo \
           file://0001-configure-Prune-PIE-flags.patch \
           file://fallback-passwd \
           file://fallback-group \
           file://moreretries.patch \
           file://toomanyfiles.patch \
           "

SRCREV = "fddbe854c9db058d5a05830d3bcdd4233d95ee2e"
S = "${WORKDIR}/git"
PV = "1.9.0+git${SRCPV}"

