require ruby.inc

SRC_URI += " \
           file://ruby-CVE-2017-9226.patch \
           file://ruby-CVE-2017-9228.patch \
           file://CVE-2018-1000073.patch \
           "

SRC_URI[md5sum] = "23867bc8c16c55e43b14dfe0614bcfa8"
SRC_URI[sha256sum] = "dac81822325b79c3ba9532b048c2123357d3310b2b40024202f360251d9829b1"

# it's unknown to configure script, but then passed to extconf.rb
# maybe it's not really needed as we're hardcoding the result with
# 0001-socket-extconf-hardcode-wide-getaddr-info-test-outco.patch
UNKNOWN_CONFIGURE_WHITELIST += "--enable-wide-getaddrinfo"

PACKAGECONFIG ??= ""
PACKAGECONFIG += "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"

PACKAGECONFIG[valgrind] = "--with-valgrind=yes, --with-valgrind=no, valgrind"
PACKAGECONFIG[gmp] = "--with-gmp=yes, --with-gmp=no, gmp"
PACKAGECONFIG[ipv6] = ",--enable-wide-getaddrinfo,"

EXTRA_AUTORECONF += "--exclude=aclocal"

EXTRA_OECONF = "\
    --disable-versioned-paths \
    --disable-rpath \
    --disable-dtrace \
    --enable-shared \
    --enable-load-relative \
"

do_install() {
    oe_runmake 'DESTDIR=${D}' install
}

do_install_append_class-target () {
    # Find out rbconfig.rb from .installed.list
    rbconfig_rb=`grep rbconfig.rb ${B}/.installed.list`
    # Remove build host directories
    sed -i -e 's:--sysroot=${STAGING_DIR_TARGET}::g' \
           -e s:'--with-libtool-sysroot=${STAGING_DIR_TARGET}'::g \
           -e 's|${DEBUG_PREFIX_MAP}||g' \
           -e 's:${HOSTTOOLS_DIR}/::g' \
           -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
           -e 's:${RECIPE_SYSROOT}::g' \
           -e 's:${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}::g' \
        ${D}$rbconfig_rb

    # Find out created.rid from .installed.list
    created_rid=`grep created.rid ${B}/.installed.list`
    # Remove build host directories
    sed -i -e 's:${WORKDIR}::g' ${D}$created_rid

}

PACKAGES =+ "${PN}-ri-docs ${PN}-rdoc"

SUMMARY_${PN}-ri-docs = "ri (Ruby Interactive) documentation for the Ruby standard library"
RDEPENDS_${PN}-ri-docs = "${PN}"
FILES_${PN}-ri-docs += "${datadir}/ri"

SUMMARY_${PN}-rdoc = "RDoc documentation generator from Ruby source"
RDEPENDS_${PN}-rdoc = "${PN}"
FILES_${PN}-rdoc += "${libdir}/ruby/*/rdoc ${bindir}/rdoc"

FILES_${PN} += "${datadir}/rubygems"

BBCLASSEXTEND = "native"
