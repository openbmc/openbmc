SUMMARY = "A GPE application that allows you to take screenshots"
SECTION = "gpe"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

DEPENDS = "glib-2.0 gtk+ libgpewidget libglade libsoup-2.4"

PR = "r3"

SRC_URI[md5sum] = "eaf545561b0ad981c9d01833f30fcf95"
SRC_URI[sha256sum] = "762778421fae7c62d5ec6a9d27986166c0dbbe2ff51fc10bb9b8baff5c367534"

GPE_TARBALL_SUFFIX = "bz2"

inherit gpe autotools pkgconfig

SRC_URI += "file://0001-Fix-the-ordering-of-LDADD-options-to-fix-a-compilati.patch \
    file://use.libsoup-2.4.patch \
"

RREPLACES_${PN} = "gpe-screenshot"

