SUMMARY = "A graphical front-end for gcov"
HOMEPAGE = "https://github.com/linux-test-project/lcov"
DESCRIPTION = "LCOV is a graphical front-end for GCC's coverage testing \
tool gcov. It collects gcov data for multiple source files and creates \
HTML pages containing the source code annotated with coverage information. \
It also adds overview pages for easy navigation within the file structure. \
LCOV supports statement, function and branch coverage measurement."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

RDEPENDS:${PN} += " \
    libjson-perl \
    perl \
    perl-module-compress-zlib \
    perl-module-constant \
    perl-module-cwd \
    perl-module-digest-md5 \
    perl-module-digest-sha \
    perl-module-errno \
    perl-module-file-basename \
    perl-module-file-copy \
    perl-module-file-find \
    perl-module-file-path \
    perl-module-file-spec \
    perl-module-file-spec-functions \
    perl-module-file-spec-unix \
    perl-module-file-temp \
    perl-module-filehandle \
    perl-module-getopt-long \
    perl-module-getopt-std \
    perl-module-list-util \
    perl-module-load \
    perl-module-metadata \    
    perl-module-mro \
    perl-module-overload \
    perl-module-overload-numbers \
    perl-module-overloading \
    perl-module-parent \
    perl-module-pod-usage \
    perl-module-posix \
    perl-module-re \
    perl-module-safe \
    perl-module-scalar-util \
    perl-module-term-cap \
    perl-module-text-parsewords \
    perl-module-tie-hash \
"

RDEPENDS:${PN}:append:class-target = " \
    gcov \
    gcov-symlinks \
"
SRC_URI = "https://github.com/linux-test-project/lcov/releases/download/v${PV}/lcov-${PV}.tar.gz"
SRC_URI[sha256sum] = "3457825c6b2fe4ef77c782b82a23875c84a3c955243823f05d8f2dec0d455820"

UPSTREAM_CHECK_URI = "https://github.com/linux-test-project/lcov/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

do_install() {
    # Pass the runtime PREFIX and the staging DESTDIR separately. lcov's
    # Makefile bakes the runtime --bindir/--libdir ($(PREFIX)/...) into the
    # installed perl modules and manpages, so folding ${D} into PREFIX would
    # embed the build path and trip the buildpaths QA check.
    oe_runmake install PREFIX=${prefix} DESTDIR=${D} CFG_DIR=${sysconfdir} LCOV_PERL_PATH="/usr/bin/env perl"

    # The upstream test suite is shipped under ${datadir}/lcov/tests and is
    # not needed at runtime. Its shell scripts pull in unwanted file-rdeps on
    # /bin/bash, /usr/bin/bash and /bin/env, so drop it from the package.
    rm -rf ${D}${datadir}/lcov/tests
}

BBCLASSEXTEND = "native nativesdk"
