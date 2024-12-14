SUMMARY = "An interpreter of object-oriented scripting language"
DESCRIPTION = "Ruby is an interpreted scripting language for quick \
and easy object-oriented programming. It has many features to process \
text files and to do system management tasks (as in Perl). \
It is simple, straight-forward, and extensible. \
"
HOMEPAGE = "http://www.ruby-lang.org/"
SECTION = "devel/ruby"
LICENSE = "Ruby | BSD-2-Clause | BSD-3-Clause | GPL-2.0-only | ISC | MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=5b8c87559868796979806100db3f3805 \
                    file://BSDL;md5=8b50bc6de8f586dc66790ba11d064d75 \
                    file://GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LEGAL;md5=81e6a4d81533b9263da4c3485a0ad883 \
                    "

DEPENDS = "zlib openssl libyaml libffi"
DEPENDS:append:class-target = " ruby-native"
DEPENDS:append:class-nativesdk = " ruby-native"

SHRT_VER = "${@oe.utils.trim_version("${PV}", 2)}"
SRC_URI = "http://cache.ruby-lang.org/pub/ruby/${SHRT_VER}/ruby-${PV}.tar.gz \
           file://0001-extmk-fix-cross-compilation-of-external-gems.patch \
           file://0002-Obey-LDFLAGS-for-the-link-of-libruby.patch \
           file://run-ptest \
           file://0003-rdoc-build-reproducible-documentation.patch \
           file://0004-lib-mkmf.rb-sort-list-of-object-files-in-generated-M.patch \
           file://0005-Mark-Gemspec-reproducible-change-fixing-784225-too.patch \
           file://0006-Make-gemspecs-reproducible.patch \
           file://0001-vm_dump.c-Define-REG_S1-and-REG_S2-for-musl-riscv.patch \
           "
UPSTREAM_CHECK_URI = "https://www.ruby-lang.org/en/downloads/"

inherit autotools ptest pkgconfig


# This snippet lets compiled extensions which rely on external libraries,
# such as zlib, compile properly.  If we don't do this, then when extmk.rb
# runs, it uses the native libraries instead of the target libraries, and so
# none of the linking operations succeed -- which makes extconf.rb think
# that the libraries aren't available and hence that the extension can't be
# built.

do_configure:prepend() {
    sed -i "s#%%TARGET_CFLAGS%%#$CFLAGS#; s#%%TARGET_LDFLAGS%%#$LDFLAGS#" ${S}/common.mk
    rm -rf ${S}/ruby/
}

DEPENDS:append:libc-musl = " libucontext"

SRC_URI[sha256sum] = "8dc48fffaf270f86f1019053f28e51e4da4cce32a36760a0603a9aee67d7fd8d"

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
    --with-mantype=man \
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
    install -D ${S}/tool/test/init.rb ${D}${PTEST_PATH}/tool/test/init.rb
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

BBCLASSEXTEND = "native nativesdk"
