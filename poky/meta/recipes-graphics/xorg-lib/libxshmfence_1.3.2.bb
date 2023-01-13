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

SRC_URI[sha256sum] = "870df257bc40b126d91b5a8f1da6ca8a524555268c50b59c0acd1a27f361606f"

BBCLASSEXTEND = "native nativesdk"
