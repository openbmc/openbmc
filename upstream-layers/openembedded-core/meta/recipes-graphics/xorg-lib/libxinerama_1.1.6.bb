require xorg-lib-common.inc

SUMMARY = "Xinerama: Xinerama extension library"

DESCRIPTION = "Xinerama is a simple library designed to interface the \
Xinerama Extension for retrieving information about physical output \
devices which may be combined into a single logical X screen."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=68f2159f2eadc772815f13d9565b453d \
                    file://src/Xinerama.c;beginline=2;endline=25;md5=fcef273bfb66339256411dd06ea79c02 \
                    "

DEPENDS += "libxext xorgproto"
PROVIDES = "xinerama"
PE = "1"

XORG_PN = "libXinerama"
SRC_URI[sha256sum] = "d00fc1599c303dc5cbc122b8068bdc7405d6fcb19060f4597fc51bd3a8be51d7"

BBCLASSEXTEND = "native nativesdk"
