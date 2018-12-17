SUMMARY = "Debugging and profiling extension for PHP"
LICENSE = "Xdebug"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34df3a274aa12b795417c65634c07f16"

DEPENDS = "php re2c-native"

SRC_URI = "http://xdebug.org/files/xdebug-${PV}.tgz"

SRC_URI[md5sum] = "35cc0f2e8b7d40556a9ad9afd9c49aee"
SRC_URI[sha256sum] = "dae691d6c052073b886e0c6e1306a707bca9fd18a1e3485384ef6c4aacf1bd77"

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

FILES_${PN} += "${libdir}/php*/extensions/*/*.so"
FILES_${PN}-dbg += "${libdir}/php*/extensions/*/.debug"
