SUMMARY = "xdotool - command-line X11 automation tool - utilising X11 XTEST interface"
HOMEPAGE = "https://github.com/jordansissel/xdotool"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=2f9cbf7e9401cec8a38666a08851ce6b"
SECTION = "x11"
DEPENDS = "virtual/libx11 libxtst libxinerama libxkbcommon"

inherit features_check pkgconfig perlnative
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

# The release tarball ships a prebuilt (and wrongly pathed) libxdo.pc but omits
# pc.sh, which the Makefile's installpc rule needs to (re)generate libxdo.pc.
# Depending on the unpack mtime ordering of VERSION vs libxdo.pc, make tries to
# rebuild libxdo.pc and fails with "sh: 0: cannot open pc.sh". Supply the
# upstream pc.sh so regeneration works with the correct libdir/includedir.
SRC_URI = "https://github.com/jordansissel/${BPN}/releases/download/v${PV}/${BP}.tar.gz \
           file://pc.sh"
SRC_URI[sha256sum] = "eee789b00d6a13d47b31bbc139727e6408c21b5f6ba5e804fdf6ecfb8c781356"

EXTRA_OEMAKE = "PREFIX=${prefix} INSTALLLIB=${libdir} INSTALLMAN=${mandir} WITHOUT_RPATH_FIX=1"

UPSTREAM_CHECK_URI = "https://github.com/jordansissel/xdotool/tags"
UPSTREAM_CHECK_REGEX = "v(?P<pver>\d+\.\d{8}\.\d+)"

do_configure:append() {
    install -m 0755 ${UNPACKDIR}/pc.sh ${S}/pc.sh
}

do_install() {
    oe_runmake install DESTDIR=${D} PREFIX=${prefix} LDCONFIG=true
}
