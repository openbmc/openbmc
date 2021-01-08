require pseudo.inc

SRC_URI = "git://git.yoctoproject.org/pseudo;branch=oe-core \
           file://0001-configure-Prune-PIE-flags.patch \
           file://fallback-passwd \
           file://fallback-group \
           "

SRCREV = "1ee9a1e05e200f189f6644411ba9304e8a479b0b"
S = "${WORKDIR}/git"
PV = "1.9.0+git${SRCPV}"

# error: use of undeclared identifier '_STAT_VER'
COMPATIBLE_HOST_libc-musl = 'null'
