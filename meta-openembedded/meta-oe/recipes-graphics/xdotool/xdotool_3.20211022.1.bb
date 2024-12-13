SUMMARY = "xdotool - command-line X11 automation tool - utilising X11 XTEST interface"
HOMEPAGE = "https://github.com/jordansissel/xdotool"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=2f9cbf7e9401cec8a38666a08851ce6b"
SECTION = "x11"
DEPENDS = "virtual/libx11 libxtst libxinerama libxkbcommon"

inherit features_check pkgconfig perlnative
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "https://github.com/jordansissel/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "96f0facfde6d78eacad35b91b0f46fecd0b35e474c03e00e30da3fdd345f9ada"

EXTRA_OEMAKE = "PREFIX=${prefix} INSTALLLIB=${libdir} INSTALLMAN=${mandir}"

UPSTREAM_CHECK_URI="https://github.com/jordansissel/xdotool/tags"
UPSTREAM_CHECK_REGEX = "v(?P<pver>\d+\.\d{8}\.\d+)"

do_install() {
    oe_runmake install DESTDIR=${D} PREFIX=${prefix}
}
