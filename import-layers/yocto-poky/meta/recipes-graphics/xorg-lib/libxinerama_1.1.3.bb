require xorg-lib-common.inc

SUMMARY = "Xinerama: Xinerama extension library"

DESCRIPTION = "Xinerama is a simple library designed to interface the \
Xinerama Extension for retrieving information about physical output \
devices which may be combined into a single logical X screen."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6f4f634d1643a2e638bba3fcd19c2536 \
                    file://src/Xinerama.c;beginline=2;endline=25;md5=fcef273bfb66339256411dd06ea79c02"

DEPENDS += "libxext xineramaproto"
PROVIDES = "xinerama"
PE = "1"

XORG_PN = "libXinerama"

SRC_URI[md5sum] = "9336dc46ae3bf5f81c247f7131461efd"
SRC_URI[sha256sum] = "7a45699f1773095a3f821e491cbd5e10c887c5a5fce5d8d3fced15c2ff7698e2"

BBCLASSEXTEND = "native nativesdk"
