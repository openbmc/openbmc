require libmemcached.inc

SRC_URI += "\
           file://crosscompile.patch \
           file://0001-configure.ac-Do-not-configure-build-aux.patch \
           file://0001-Fix-comparison-types.patch \
           file://0002-POSIX_SPAWN_USEVFORK-is-not-linux-specific-but-glibc.patch \
           "
SRC_URI[md5sum] = "b3958716b4e53ddc5992e6c49d97e819"
SRC_URI[sha256sum] = "e22c0bb032fde08f53de9ffbc5a128233041d9f33b5de022c0978a2149885f82"
