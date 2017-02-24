require ofono.inc

SRC_URI  = "\
  ${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
  file://ofono \
"
SRC_URI[md5sum] = "0a6b37c8ace891cb2a7ca5d121043a0a"
SRC_URI[sha256sum] = "53cdbf342913f46bce4827241c60e24255a3d43a94945edf77482ae5b312d51f"

CFLAGS_append_libc-uclibc = " -D_GNU_SOURCE"
