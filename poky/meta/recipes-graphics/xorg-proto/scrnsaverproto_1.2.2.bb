require xorg-proto-common.inc

SUMMARY = "Xscrnsaver: X Screen Saver extension headers"

DESCRIPTION = "This package provides the wire protocol for the X Screen \
Saver extension.  This extension allows an external \"screen saver\" \
client to detect when the alternative image is to be displayed and to \
provide the graphics."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=eed49b78b15b436c933b6b8b054e3901 \
                    file://saverproto.h;endline=26;md5=a84c0637305159f3c0ab173aaeede48d"

PE = "1"

EXTRA_OECONF_append = " --enable-specs=no"

SRC_URI[md5sum] = "edd8a73775e8ece1d69515dd17767bfb"
SRC_URI[sha256sum] = "8bb70a8da164930cceaeb4c74180291660533ad3cc45377b30a795d1b85bcd65"
