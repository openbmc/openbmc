SUMMARY = "Debugging and profiling extension for PHP"
LICENSE = "Xdebug"
LIC_FILES_CHKSUM = "file://LICENSE;md5=92d94a330d34ee6edc2638450736f119"

DEPENDS = "php re2c-native"

SRC_URI = "http://xdebug.org/files/xdebug-${PV}.tgz"

SRC_URI[sha256sum] = "9be3ae0fdb4dc4a4c68084626cddc56f12396487e309a8c8dd318f0f900d1a68"

UPSTREAM_CHECK_REGEX = "xdebug-(?P<pver>\d+(\.\d+)+)\.tgz"

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
