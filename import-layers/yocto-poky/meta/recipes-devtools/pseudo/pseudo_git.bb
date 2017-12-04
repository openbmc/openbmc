require pseudo.inc

SRCREV = "45eca34c754d416a38bee90fb2d3c110a0b6cc5f"
PV = "1.8.1+git${SRCPV}"

DEFAULT_PREFERENCE = "-1"

SRC_URI = "git://git.yoctoproject.org/pseudo \
           file://0001-configure-Prune-PIE-flags.patch \
           file://fallback-passwd \
           file://fallback-group \
           file://moreretries.patch"

S = "${WORKDIR}/git"

