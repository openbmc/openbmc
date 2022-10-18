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

SRC_URI[md5sum] = "42dda8016943dc12aff2c03a036e0937"
SRC_URI[sha256sum] = "b884300d26a14961a076fbebc762a39831cb75f92bed5ccf9836345b459220c7"

BBCLASSEXTEND = "native nativesdk"
