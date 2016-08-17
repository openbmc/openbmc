SUMMARY = "Generic PCI access library for X"

DESCRIPTION = "libpciaccess provides functionality for X to access the \
PCI bus and devices in a platform-independent way."

require xorg-lib-common.inc

SRC_URI += "\
            file://0001-Include-config.h-before-anything-else-in-.c.patch \
            file://0002-Fix-quoting-issue.patch \
            file://0003-linux_sysfs.c-Include-limits.h-for-PATH_MAX.patch \
            file://0004-Don-t-include-sys-io.h-on-arm.patch \
"

SRC_URI[md5sum] = "ace78aec799b1cf6dfaea55d3879ed9f"
SRC_URI[sha256sum] = "07f864654561e4ac8629a0ef9c8f07fbc1f8592d1b6c418431593e9ba2cf2fcf"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=277aada5222b9a22fbf3471ff3687068"

REQUIRED_DISTRO_FEATURES = ""
