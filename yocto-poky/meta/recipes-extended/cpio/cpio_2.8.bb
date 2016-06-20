require cpio_v2.inc

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b7f772ea3a2489231cb4872656cac34b"

PR = "r4"

SRC_URI += " \
	    file://statdef.patch \
	    file://m4extensions.patch \
	    file://avoid_heap_overflow.patch \
	    file://fix-memory-overrun.patch \
	   "

SRC_URI[md5sum] = "0caa356e69e149fb49b76bacc64615a1"
SRC_URI[sha256sum] = "1b203248874c3b5a728b351f06513e5282f73e0170b7f207fbf8c39f28f6b4ad"

# Required to build with gcc 4.3 and later:
CFLAGS += "-fgnu89-inline"
