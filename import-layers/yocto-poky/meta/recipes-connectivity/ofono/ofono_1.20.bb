require ofono.inc

SRC_URI  = "\
  ${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
  file://ofono \
"
SRC_URI[md5sum] = "fad0630fce6a9aecdb7db37bc1f1db7d"
SRC_URI[sha256sum] = "5d7ba8f481a7715d013a79f8d6477eb89d8aaae399395d5d008a1317c34a31d5"
