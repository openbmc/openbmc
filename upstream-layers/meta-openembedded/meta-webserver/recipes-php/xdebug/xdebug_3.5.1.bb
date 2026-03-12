SUMMARY = "Debugging and profiling extension for PHP"
HOMEPAGE = "https://xdebug.org/"
LICENSE = "Xdebug"
LIC_FILES_CHKSUM = "file://LICENSE;md5=afd6ce4aa04fdc346e5b3c6e634bd75c"

DEPENDS = "php re2c-native"

SRC_URI = "https://xdebug.org/files/xdebug-${PV}.tgz"

SRC_URI[sha256sum] = "0f26849a5edf3d9120edc100219854599d54f923a8a4d1cb4fe4403520e49678"

UPSTREAM_CHECK_URI = "https://github.com/xdebug/xdebug/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/(?P<pver>\d+(\.\d+)+)$"

inherit autotools

EXTRA_OECONF += "--enable-xdebug -with-php-config=${STAGING_BINDIR_CROSS}/php-config"

do_configure() {
    cd ${S}
    ${STAGING_BINDIR_CROSS}/phpize
    cd ${B}

    # Running autoreconf as autotools_do_configure would do here
    # breaks the libtool configuration resulting in a failure later
    # in do_compile. It's possible this may be fixable, however the
    # easiest course of action for the moment is to avoid doing that.
    oe_runconf
}

do_install() {
    oe_runmake install INSTALL_ROOT=${D}
}

FILES:${PN} += "${libdir}/php*/extensions/*/*.so"
FILES:${PN}-dbg += "${libdir}/php*/extensions/*/.debug"
