SUMMARY = "Generic PCI access library for X"

DESCRIPTION = "libpciaccess provides functionality for X to access the \
PCI bus and devices in a platform-independent way."

require xorg-lib-common.inc

SRC_URI += "\
            file://0004-Don-t-include-sys-io.h-on-arm.patch \
"

SRC_URI[md5sum] = "d810ab17e24c1418dedf7207fb2841d4"
SRC_URI[sha256sum] = "752c54e9b3c311b4347cb50aea8566fa48eab274346ea8a06f7f15de3240b999"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=277aada5222b9a22fbf3471ff3687068"

REQUIRED_DISTRO_FEATURES = ""
