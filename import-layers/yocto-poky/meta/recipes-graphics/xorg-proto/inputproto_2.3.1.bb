require xorg-proto-common.inc

SUMMARY = "XI: X Input extension headers"

DESCRIPTION = "This package provides the wire protocol for the X Input \
extension.  The extension supports input devices other then the core X \
keyboard and pointer."

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=e562cc0f6587b961f032211d8160f31e \
                    file://XI2proto.h;endline=48;md5=1ac1581e61188da2885cc14ff49b20be"

PE = "1"

inherit gettext

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "6caebead4b779ba031727f66a7ffa358"
SRC_URI[sha256sum] = "5a47ee62053a6acef3a83f506312494be1461068d0b9269d818839703b95c1d1"
