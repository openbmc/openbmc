require xorg-doc-common.inc
PE = "1"
PR = "${INC_PR}.0"

DEPENDS += "util-macros"

LIC_FILES_CHKSUM = "file://COPYING;md5=c8c6c808cd3c797a07b24e443af1c449"

SRC_URI[md5sum] = "2588efb3f49f7fc6ecf41ce42e0b2e5e"
SRC_URI[sha256sum] = "84fd94e5c50556e6f77501485f8a48724cf3c95c6d58480bc280258ba14580c8"

FILES_${PN} += " /usr/share/sgml/X11"
