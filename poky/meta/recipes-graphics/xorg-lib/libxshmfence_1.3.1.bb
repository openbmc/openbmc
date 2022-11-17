SUMMARY = "Shared memory 'SyncFence' synchronization primitive"

DESCRIPTION = "This library offers a CPU-based synchronization primitive compatible \
with the X SyncFence objects that can be shared between processes \
using file descriptor passing."

require xorg-lib-common.inc

LICENSE = "HPND"
LIC_FILES_CHKSUM = "file://COPYING;md5=47e508ca280fde97906eacb77892c3ac"

DEPENDS += "virtual/libx11"

EXTRA_OECONF += "--with-shared-memory-dir=/dev/shm"

SRC_URI += "file://0001-xshmfence_futex.h-Define-SYS_futex-if-it-does-not-ex.patch"

XORG_EXT = "tar.xz"
SRC_URI[sha256sum] = "1129f95147f7bfe6052988a087f1b7cb7122283d2c47a7dbf7135ce0df69b4f8"

BBCLASSEXTEND = "native nativesdk"
