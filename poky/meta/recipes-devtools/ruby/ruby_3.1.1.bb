require ruby.inc

DEPENDS:append:libc-musl = " libucontext"

SRC_URI += " \
           file://remove_has_include_macros.patch \
           file://run-ptest \
           file://0001-template-Makefile.in-do-not-write-host-cross-cc-item.patch \
           file://0002-template-Makefile.in-filter-out-f-prefix-map.patch \
           file://0003-rdoc-build-reproducible-documentation.patch \
           file://0004-lib-mkmf.rb-sort-list-of-object-files-in-generated-M.patch \
           file://0005-Mark-Gemspec-reproducible-change-fixing-784225-too.patch \
           file://0006-Make-gemspecs-reproducible.patch \
           file://0001-vm_dump.c-Define-REG_S1-and-REG_S2-for-musl-riscv.patch \
           "

SRC_URI[sha256sum] = "fe6e4782de97443978ddba8ba4be38d222aa24dc3e3f02a6a8e7701c0eeb619d"

PACKAGECONFIG ??= ""
PACKAGECONFIG += "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"

PACKAGECONFIG[valgrind] = "--with-valgrind=yes, --with-valgrind=no, valgrind"
PACKAGECONFIG[gmp] = "--with-gmp=yes, --with-gmp=no, gmp"
PACKAGECONFIG[ipv6] = "--enable-ipv6, --disable-ipv6,"
# rdoc is off by default due to non-reproducibility reported in
# https://bugs.ruby-lang.org/issues/18456
PACKAGECONFIG[rdoc] = "--enable-install-rdoc,--disable-install-rdoc,"

EXTRA_OECONF = "\
    --disable-versioned-paths \
    --disable-rpath \
    --disable-dtrace \
    --enable-shared \
    --enable-load-relative \
    --with-pkg-config=pkg-config \
    --with-static-linked-ext \
"

EXTRA_OECONF:append:libc-musl = "\
    ac_cv_func_isnan=yes \
    ac_cv_func_isinf=yes \
"

PARALLEL_MAKEINST = ""

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

    # logs that may contain host-specific paths
    find ${D} -name gem_make.out -delete
}

do_install_ptest () {
    cp -rf ${S}/test ${D}${PTEST_PATH}/

    install -D ${S}/tool/test/runner.rb ${D}${PTEST_PATH}/tool/test/runner.rb
    cp -r ${S}/tool/lib ${D}${PTEST_PATH}/tool/
    mkdir -p ${D}${PTEST_PATH}/lib
    cp -r ${S}/lib/did_you_mean ${S}/lib/rdoc ${D}${PTEST_PATH}/lib

    # install test-binaries
    # These .so files have sporadic reproducibility fails as seen here:
    # https://autobuilder.yocto.io/pub/repro-fail/oe-reproducible-20220107-rm1diuww/packages/diff-html/
    # As they are needed only in ruby-ptest, and that is currently altogether disabled, let's take them out.
    # If someone wants to look at where the non-determinism comes from, one possible reason is use of
    # -rdynamic -Wl,-export-dynamic
    #find $(find ./.ext -path '*/-test-') -name '*.so' -print0 \
    #    | tar --no-recursion --null -T - --no-same-owner --preserve-permissions -cf - \
    #    | tar -C ${D}${libdir}/ruby/${SHRT_VER}.0/ --no-same-owner --preserve-permissions --strip-components=2 -xf -
    # adjust path to not assume build directory layout
    sed -e 's|File.expand_path(.*\.\./bin/erb[^)]*|File.expand_path("${bindir}/erb"|g' \
        -i ${D}${PTEST_PATH}/test/erb/test_erb_command.rb

    cp -r ${S}/include ${D}/${libdir}/ruby/
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
