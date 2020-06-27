require pseudo.inc

SRC_URI = "git://git.yoctoproject.org/pseudo \
           file://0001-configure-Prune-PIE-flags.patch \
           file://fallback-passwd \
           file://fallback-group \
           file://moreretries.patch \
           file://toomanyfiles.patch \
           file://0001-maketables-wrappers-use-Python-3.patch \
           file://0001-Add-statx.patch \
           file://0001-realpath.c-Remove-trailing-slashes.patch \
           file://0006-xattr-adjust-for-attr-2.4.48-release.patch \
           file://seccomp.patch \
           file://0001-pseudo-On-a-DB-fixup-remove-files-that-do-not-exist-.patch \
           file://0001-pseudo_ipc.h-Fix-enum-typedef.patch \
           file://xattr_version.patch \
           "

SRCREV = "060058bb29f70b244e685b3c704eb0641b736f73"
S = "${WORKDIR}/git"
PV = "1.9.0+git${SRCPV}"

# error: use of undeclared identifier '_STAT_VER'
COMPATIBLE_HOST_libc-musl = 'null'
