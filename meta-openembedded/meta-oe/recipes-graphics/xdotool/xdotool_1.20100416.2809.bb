SUMMARY = "xdotool - command-line X11 automation tool - utilising X11 XTEST interface"
HOMEPAGE = "http://www.semicomplete.com/projects/xdotool"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=2f9cbf7e9401cec8a38666a08851ce6b"
SECTION = "x11"
DEPENDS = "virtual/libx11 libxtst"

PR = "r1"

inherit features_check pkgconfig perlnative
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "http://semicomplete.googlecode.com/files/xdotool-${PV}.tar.gz"
SRC_URI[md5sum] = "1d5be641e512c343abfe5f78b39e6f19"
SRC_URI[sha256sum] = "42d7271fbc796e53db71bb221f311b9ff3c51d90a71c9487a9bd3101ca39894f"

EXTRA_OEMAKE = "PREFIX=${prefix} INSTALLLIB=${libdir} INSTALLMAN=${mandir}"

do_install() {
    oe_runmake -e install DESTDIR=${D} PREFIX=${prefix}
}

