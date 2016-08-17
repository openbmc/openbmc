SUMMARY = "Flexible CLI player in the spirit of mpg123, based on GStreamer"
HOMEPAGE = "http://space.twc.de/~stefan/gst123.php"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2 \
                    file://src/gst123.cc;beginline=1;endline=19;md5=05d2f5d54b985b986c26af931d2084f8"

DEPENDS = "libx11 gstreamer gst-plugins-base gtk+ ncurses"

SRC_URI = "http://space.twc.de/~stefan/gst123/${BPN}-${PV}.tar.bz2 \
           file://display.patch"

SRC_URI[md5sum] = "1e77767c9d6fecee5641f95804f160fe"
SRC_URI[sha256sum] = "89d1de025eca0466c125dcd6a11b64341bdf98ee4c03c3e5a12321d77cb8b0ce"

inherit autotools

PNBLACKLIST[gst123] ?= "gst123 is still "sometimes" using wrong sysroot"
