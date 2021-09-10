require ruby.inc

DEPENDS:append:libc-musl = " libucontext"

SRC_URI += " \
           file://remove_has_include_macros.patch \
           file://run-ptest \
           file://0001-template-Makefile.in-do-not-write-host-cross-cc-item.patch \
           file://0002-template-Makefile.in-filter-out-f-prefix-map.patch \
           "

SRC_URI[sha256sum] = "5085dee0ad9f06996a8acec7ebea4a8735e6fac22f22e2d98c3f2bc3bef7e6f1"

PACKAGECONFIG ??= ""
PACKAGECONFIG += "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"

PACKAGECONFIG[valgrind] = "--with-valgrind=yes, --with-valgrind=no, valgrind"
PACKAGECONFIG[gmp] = "--with-gmp=yes, --with-gmp=no, gmp"
PACKAGECONFIG[ipv6] = "--enable-ipv6, --disable-ipv6,"

EXTRA_OECONF = "\
    --disable-versioned-paths \
    --disable-rpath \
    --disable-dtrace \
    --enable-shared \
    --enable-load-relative \
    --with-pkg-config=pkg-config \
"

EXTRA_OECONF:append:libc-musl = "\
    LIBS='-lucontext' \
    ac_cv_func_isnan=yes \
    ac_cv_func_isinf=yes \
"

EXTRA_OECONF:append:libc-musl:riscv64 = "\
    --with-coroutine=copy \
"
EXTRA_OECONF:append:libc-musl:riscv32 = "\
    --with-coroutine=copy \
"

do_install() {
    oe_runmake 'DESTDIR=${D}' install
}

do_install:append:class-target () {
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

    sed -i -e 's|${DEBUG_PREFIX_MAP}||g' \
        ${D}${libdir}/pkgconfig/*.pc

}

do_install_ptest () {
    cp -rf ${S}/test ${D}${PTEST_PATH}/

    install -D ${S}/tool/test/runner.rb ${D}${PTEST_PATH}/tool/test/runner.rb
    cp -r ${S}/tool/lib ${D}${PTEST_PATH}/tool/
    mkdir -p ${D}${PTEST_PATH}/lib
    cp -r ${S}/lib/did_you_mean ${S}/lib/rdoc ${D}${PTEST_PATH}/lib

    # install test-binaries
    find $(find ./.ext -path '*/-test-') -name '*.so' -print0 \
        | tar --no-recursion --null -T - --no-same-owner --preserve-permissions -cf - \
        | tar -C ${D}${libdir}/ruby/${SHRT_VER}.0/ --no-same-owner --preserve-permissions --strip-components=2 -xf -
    # adjust path to not assume build directory layout
    sed -e 's|File.expand_path(.*\.\./bin/erb[^)]*|File.expand_path("${bindir}/erb"|g' \
        -i ${D}${PTEST_PATH}/test/erb/test_erb_command.rb

    cp -r ${S}/include ${D}/${libdir}/ruby/
    test_case_rb=`grep rubygems/test_case.rb ${B}/.installed.list`
    sed -i -e 's:../../../test/:../../../ptest/test/:g' ${D}/$test_case_rb
}

PACKAGES =+ "${PN}-ri-docs ${PN}-rdoc"

SUMMARY:${PN}-ri-docs = "ri (Ruby Interactive) documentation for the Ruby standard library"
RDEPENDS:${PN}-ri-docs = "${PN}"
FILES:${PN}-ri-docs += "${datadir}/ri"

SUMMARY:${PN}-rdoc = "RDoc documentation generator from Ruby source"
RDEPENDS:${PN}-rdoc = "${PN}"
FILES:${PN}-rdoc += "${libdir}/ruby/*/rdoc ${bindir}/rdoc"

FILES:${PN} += "${datadir}/rubygems"

FILES:${PN}-ptest:append:class-target = "\
    ${libdir}/ruby/include \
    ${libdir}/ruby/${SHRT_VER}.0/*/-test- \
"

BBCLASSEXTEND = "native"
