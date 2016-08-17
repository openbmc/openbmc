SUMMARY = "A lightweight PDF viewer based on gtk and poppler"
HOMEPAGE = "http://www.emma-soft.com/projects/epdfview/"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"
SECTION = "x11/applications"
DEPENDS = "gtk+ poppler"

PR = "r3"

EXTRA_OECONF += "--without-cups"

inherit autotools gettext

SRC_URI = "http://www.emma-soft.com/projects/${BPN}/chrome/site/releases/${BP}.tar.bz2 \
           file://browser_command.patch \
           file://fix-format.patch \
           file://glib-single-include.patch \
           file://swap-colors.patch \
"
SRC_URI[md5sum] = "e50285b01612169b2594fea375f53ae4"
SRC_URI[sha256sum] = "948648ae7c9d7b3b408d738bd4f48d87375b1196cae1129d6b846a8de0f2f8f0"

PNBLACKLIST[epdfview] ?= "BROKEN: images are not displayed anymore"
# There will be no further development / upstream is gone [1]
# [1] https://bugzilla.redhat.com/show_bug.cgi?id=906121
