SUMMARY = "Debugging and profiling extension for PHP"
LICENSE = "Xdebug"
LIC_FILES_CHKSUM = "file://LICENSE;md5=92d94a330d34ee6edc2638450736f119"

DEPENDS = "php re2c-native"

SRC_URI = "http://xdebug.org/files/xdebug-${PV}.tgz"

SRC_URI[md5sum] = "c04be1bf225b768bf627dc92e5a1f9df"
SRC_URI[sha256sum] = "775b1705109611b996d6a713fe14117a67846e157eb7dbf349bc0b055e861a10"

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
