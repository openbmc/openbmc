DESCRIPTION = "Libsexy is a collection of GTK+ widgets that extend the functionality of such standard widgets as GtkEntry and GtkLabel"
HOMEPAGE = "http://wiki.chipx86.com/wiki/Libsexy"
AUTHOR = "Christian Hammond <chipx86@chipx86.com>"
SECTION = "x11/libs"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

DEPENDS = "gtk+ enchant libxml2"

SRC_URI = "http://releases.chipx86.com/libsexy/${BPN}/${BP}.tar.gz \
           file://libsexy-pkgconfig-fixes.patch"

inherit autotools pkgconfig

