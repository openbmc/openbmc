SUMMARY = "Simple web browser"
DESCRIPTION = "Simple open source web browser based on WebKit2/GTK"
HOMEPAGE = "https://surf.suckless.org/"
SECTION = "x11/graphics"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2a6f86d002ae9ae1eb1ccc466289f146"

DEPENDS = "webkitgtk3 gtk+3 glib-2.0 gcr3"

REQUIRED_DISTRO_FEATURES = "x11 opengl"

SRCREV = "48517e586cdc98bc1af7115674b554cc70c8bc2e"
SRC_URI = "git://git.suckless.org/surf;branch=surf-webkit2 \
           file://0001-config.mk-Fix-compiler-and-linker.patch \
           "
inherit pkgconfig features_check

TARGET_CC_ARCH += "${LDFLAGS}"

do_install () {
	install -D -m 0755 ${S}/surf ${D}${bindir}/surf
}
