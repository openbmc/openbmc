require pseudo.inc

SRC_URI = "git://git.yoctoproject.org/pseudo;branch=oe-core \
           file://0001-configure-Prune-PIE-flags.patch \
           file://fallback-passwd \
           file://fallback-group \
           "

SRCREV = "b988b0a6b8afd8d459bc9a2528e834f63a3d59b2"
S = "${WORKDIR}/git"
PV = "1.9.0+git${SRCPV}"

# error: use of undeclared identifier '_STAT_VER'
COMPATIBLE_HOST_libc-musl = 'null'
