SUMMARY = "Generic PCI access library for X"

DESCRIPTION = "libpciaccess provides functionality for X to access the \
PCI bus and devices in a platform-independent way."

require xorg-lib-common.inc

SRC_URI += "\
            file://0004-Don-t-include-sys-io.h-on-arm.patch \
"

SRC_URI[md5sum] = "8f436e151d5106a9cfaa71857a066d33"
SRC_URI[sha256sum] = "3df543e12afd41fea8eac817e48cbfde5aed8817b81670a4e9e493bb2f5bf2a4"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=277aada5222b9a22fbf3471ff3687068"

REQUIRED_DISTRO_FEATURES = ""
