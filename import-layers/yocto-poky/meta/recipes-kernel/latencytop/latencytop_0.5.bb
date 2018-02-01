SUMMARY = "Linux tool for measuring and fixing latency"
HOMEPAGE = "http://www.latencytop.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://latencytop.c;endline=23;md5=ee9ea9b1415356e5734adad4a87dc7fa"

inherit pkgconfig

DEPENDS = "virtual/libintl ncurses glib-2.0"

PR = "r3"

SRC_URI = "http://pkgs.fedoraproject.org/repo/pkgs/${BPN}/${BP}.tar.gz/73bb3371c6ee0b0e68e25289027e865c/${BP}.tar.gz \
            file://latencytop-makefile.patch \
            file://latencytop-fsync.patch \
            file://0001-Rectify-the-function-signatures-to-fix-prototype-mis.patch \
"

SRC_URI[md5sum] = "73bb3371c6ee0b0e68e25289027e865c"
SRC_URI[sha256sum] = "9e7f72fbea7bd918e71212a1eabaad8488d2c602205d2e3c95d62cd57e9203ef"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"

PACKAGECONFIG[x11] = ",,gtk+"

EXTRA_OEMAKE_X = "${@bb.utils.contains('PACKAGECONFIG', 'x11', 'HAS_GTK_GUI=1', '', d)}"

#CFLAGS += "${LDFLAGS}"

do_install() {
    oe_runmake install DESTDIR=${D} ${EXTRA_OEMAKE_X}
}
