require ofono.inc

SRC_URI  = "\
  ${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
  file://ofono \
  file://use-python3.patch \
"
SRC_URI[md5sum] = "be24e80f6551f46fea0c5b5879964d6c"
SRC_URI[sha256sum] = "9c8e351b7658f4b43f9a4380b731c47d2d7544a89987c48c3f227e73636c87ae"
