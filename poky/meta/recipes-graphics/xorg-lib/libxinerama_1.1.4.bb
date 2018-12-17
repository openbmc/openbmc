require xorg-lib-common.inc

SUMMARY = "Xinerama: Xinerama extension library"

DESCRIPTION = "Xinerama is a simple library designed to interface the \
Xinerama Extension for retrieving information about physical output \
devices which may be combined into a single logical X screen."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6f4f634d1643a2e638bba3fcd19c2536 \
                    file://src/Xinerama.c;beginline=2;endline=25;md5=fcef273bfb66339256411dd06ea79c02"

DEPENDS += "libxext xorgproto"
PROVIDES = "xinerama"
PE = "1"

XORG_PN = "libXinerama"

SRC_URI[md5sum] = "0d5f826a197dae74da67af4a9ef35885"
SRC_URI[sha256sum] = "0008dbd7ecf717e1e507eed1856ab0d9cf946d03201b85d5dcf61489bb02d720"

BBCLASSEXTEND = "native nativesdk"
