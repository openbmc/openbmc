require pseudo.inc

SRC_URI = "http://downloads.yoctoproject.org/releases/pseudo/${BPN}-${PV}.tar.bz2 \
           file://0001-configure-Prune-PIE-flags.patch \
           file://fallback-passwd \
           file://fallback-group \
           file://moreretries.patch \
           file://Fix-xattr-performance.patch \
           file://0001-Don-t-send-SIGUSR1-to-init.patch \
           file://0001-Quiet-diagnostics-during-startup-for-pseudo-d.patch \
           file://0002-Use-correct-file-descriptor.patch \
           file://0003-Fix-renameat-parallel-to-previous-fix-to-rename.patch \
           file://More-correctly-fix-xattrs.patch \
           "

SRC_URI[md5sum] = "ee38e4fb62ff88ad067b1a5a3825bac7"
SRC_URI[sha256sum] = "dac4ad2d21228053151121320f629d41dd5c0c87695ac4e7aea286c414192ab5"
