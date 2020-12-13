require pseudo.inc

SRC_URI = "git://git.yoctoproject.org/pseudo;branch=oe-core \
           file://0001-configure-Prune-PIE-flags.patch \
           file://fallback-passwd \
           file://fallback-group \
           file://0002-pseudo_client-Lessen-indentation-of-pseudo_client_ig.patch \
           file://0003-pseudo_client-Simplify-pseudo_client_ignore_path_chr.patch \
           "

SRCREV = "69f205c41902e17933b81b1450636848e8da2126"
S = "${WORKDIR}/git"
PV = "1.9.0+git${SRCPV}"

# error: use of undeclared identifier '_STAT_VER'
COMPATIBLE_HOST_libc-musl = 'null'
