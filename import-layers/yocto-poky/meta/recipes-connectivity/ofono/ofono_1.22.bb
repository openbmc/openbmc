require ofono.inc

SRC_URI  = "\
  ${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
  file://ofono \
  file://use-python3.patch \
"
SRC_URI[md5sum] = "2a683ab8e98448ad8bc5dc9868d2893e"
SRC_URI[sha256sum] = "8e34a6696c300c9841b55e8dff640bd3096e49f5dbe55bbebaa69a71676f687e"
