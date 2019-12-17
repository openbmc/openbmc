SUMMARY = "Generic PCI access library for X"

DESCRIPTION = "libpciaccess provides functionality for X to access the \
PCI bus and devices in a platform-independent way."

require xorg-lib-common.inc

SRC_URI += "\
"

SRC_URI[md5sum] = "b34e2cbdd6aa8f9cc3fa613fd401a6d6"
SRC_URI[sha256sum] = "214c9d0d884fdd7375ec8da8dcb91a8d3169f263294c9a90c575bf1938b9f489"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=277aada5222b9a22fbf3471ff3687068"

REQUIRED_DISTRO_FEATURES = ""

BBCLASSEXTEND = "native nativesdk"
