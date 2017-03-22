require pseudo.inc

SRCREV = "befc6dbd6469d428c9e0830dbe51bdf7ac39d9ae"
PV = "1.8.1+git${SRCPV}"

DEFAULT_PREFERENCE = "-1"

SRC_URI = "git://git.yoctoproject.org/pseudo \
           file://0001-configure-Prune-PIE-flags.patch \
           file://fallback-passwd \
           file://fallback-group \
           file://moreretries.patch"

S = "${WORKDIR}/git"

