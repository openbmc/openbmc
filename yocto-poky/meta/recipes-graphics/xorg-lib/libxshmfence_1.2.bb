SUMMARY = "Shared memory 'SyncFence' synchronization primitive"

DESCRIPTION = "This library offers a CPU-based synchronization primitive compatible \
with the X SyncFence objects that can be shared between processes \
using file descriptor passing."

require xorg-lib-common.inc

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=47e508ca280fde97906eacb77892c3ac"

DEPENDS += "virtual/libx11"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "66662e76899112c0f99e22f2fc775a7e"
SRC_URI[sha256sum] = "d21b2d1fd78c1efbe1f2c16dae1cb23f8fd231dcf891465b8debe636a9054b0c"
