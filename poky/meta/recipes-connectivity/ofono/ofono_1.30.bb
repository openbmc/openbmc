require ofono.inc

SRC_URI  = "\
  ${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
  file://ofono \
  file://0001-mbim-add-an-optional-TEMP_FAILURE_RETRY-macro-copy.patch \
"
SRC_URI[md5sum] = "2b1ce11a4db1f4b5c8cd96eb7e96ba0c"
SRC_URI[sha256sum] = "8079735efc5d7f33be9e792e791f2f7ff75c31ce67d477b994673e32319eec5c"
