SUMMARY = "Simple web browser"
DESCRIPTION = "Simple open source web browser based on WebKit2/GTK"
HOMEPAGE = "https://surf.suckless.org/"
SECTION = "x11/graphics"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2a6f86d002ae9ae1eb1ccc466289f146"

DEPENDS = "webkitgtk gtk+3 glib-2.0 gcr"

REQUIRED_DISTRO_FEATURES = "x11 opengl"

SRC_URI = "git://git.suckless.org/surf;branch=surf-webkit2 \
           file://0001-config.mk-Fix-compiler-and-linker.patch \
"
SRCREV = "bcd7d74e613fb8af11b40c351f0a6c1a771b2d2b"

S = "${WORKDIR}/git"

inherit pkgconfig features_check

TARGET_CC_ARCH += "${LDFLAGS}"

do_install () {
	install -D -m 0755 ${S}/surf ${D}${bindir}/surf
}
