require ofono.inc

SRC_URI  = "\
  ${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
  file://ofono \
  file://Revert-test-Convert-to-Python-3.patch \
  file://0001-backtrace-Disable-for-non-glibc-C-libraries.patch \
"
SRC_URI[md5sum] = "c31b5b55a1d68354bff771d3edf02829"
SRC_URI[sha256sum] = "403b98dadece8bc804c0bd16b96d3db5a3bb0f84af64b3d67924da2d1a754b07"

CFLAGS_append_libc-uclibc = " -D_GNU_SOURCE"
