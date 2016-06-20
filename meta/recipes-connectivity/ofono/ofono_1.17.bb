require ofono.inc

SRC_URI  = "\
  ${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
  file://ofono \
  file://Revert-test-Convert-to-Python-3.patch \
"
SRC_URI[md5sum] = "d280b1d267ba5bf391d2a898fea7c748"
SRC_URI[sha256sum] = "cbf20f07fd15253c682b23c1786d517f505c3688f7c4ea93da777e1523b89635"

CFLAGS_append_libc-uclibc = " -D_GNU_SOURCE"
