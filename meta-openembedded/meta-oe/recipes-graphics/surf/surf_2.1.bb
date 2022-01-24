SUMMARY = "Simple web browser"
DESCRIPTION = "Simple open source web browser based on WebKit2/GTK"
HOMEPAGE = "https://surf.suckless.org/"
SECTION = "x11/graphics"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2a6f86d002ae9ae1eb1ccc466289f146"

DEPENDS = "webkitgtk gtk+3 glib-2.0 gcr"

REQUIRED_DISTRO_FEATURES = "x11 opengl"

SRCREV = "bcd7d74e613fb8af11b40c351f0a6c1a771b2d2b"
SRC_URI = "git://git.suckless.org/surf;branch=surf-webkit2 \
           "
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'soup3', 'file://0001-config.mk-Fix-compiler-and-linke,ig.ml-make-compatible-with-webkitgtk-2.34.0.patch', '', d)}"

S = "${WORKDIR}/git"

inherit pkgconfig features_check

PACKAGECONFIG ?= ""

# Enable if soup3 is enabled in webkit recipe
PACKAGECONFIG[soup3] = ",,,"

TARGET_CC_ARCH += "${LDFLAGS}"

do_install () {
	install -D -m 0755 ${S}/surf ${D}${bindir}/surf
}
