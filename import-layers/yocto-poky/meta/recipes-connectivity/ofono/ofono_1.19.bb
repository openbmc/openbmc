require ofono.inc

SRC_URI  = "\
  ${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
  file://ofono \
"
SRC_URI[md5sum] = "a5f8803ace110511b6ff5a2b39782e8b"
SRC_URI[sha256sum] = "a0e09bdd8b53b8d2e4b54f1863ecd9aebe4786477a6cbf8f655496e8edb31c81"

CFLAGS_append_libc-uclibc = " -D_GNU_SOURCE"
