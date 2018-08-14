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

SRC_URI[md5sum] = "b290a463af7def483e6e190de460f31a"
SRC_URI[sha256sum] = "893a6af55733262058a27b38eeb1edc733669f01d404e8581b167f03c03ef31d"

EXTRA_OECONF += "--without-asciidoc"
