SUMMARY = "Simple web browser"
DESCRIPTION = "Simple open source web browser based on WebKit2/GTK"
HOMEPAGE = "https://surf.suckless.org/"
SECTION = "x11/graphics"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b57e7f7720307a02d5a6598b00fe3afa"

DEPENDS = "webkitgtk gtk+3 glib-2.0"

REQUIRED_DISTRO_FEATURES = "x11 opengl"

SRC_URI = "git://git.suckless.org/surf;branch=surf-webkit2 \
           file://0001-config.mk-Fix-compiler-and-linker.patch \
"
SRCREV = "b814567e2bf8bda07cea8de1c7a062f4aa437b65"

S = "${WORKDIR}/git"

inherit pkgconfig features_check

TARGET_CC_ARCH += "${LDFLAGS}"

do_install () {
	install -D -m 0755 ${S}/surf ${D}${bindir}/surf
}
