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
    perl \
    perl-module-filehandle \
    perl-module-getopt-std \
    perl-module-digest-md5 \
    perl-module-digest-sha \
    perl-module-constant \
    perl-module-cwd \
    perl-module-errno \
    perl-module-file-basename \
    perl-module-file-copy \
    perl-module-file-find \
    perl-module-file-path \
    perl-module-file-spec \
    perl-module-file-spec-functions \
    perl-module-file-spec-unix \
    perl-module-file-temp \
    perl-module-getopt-long \
    perl-module-list-util \
    perl-module-mro \
    perl-module-overload \
    perl-module-overloading \
    perl-module-overload-numbers \
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
SRC_URI[md5sum] = "bfee0cef50d7b7bd1df03bfadf68dcef"
SRC_URI[sha256sum] = "987031ad5528c8a746d4b52b380bc1bffe412de1f2b9c2ba5224995668e3240b"

UPSTREAM_CHECK_URI = "https://github.com/linux-test-project/lcov/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

do_install() {
    oe_runmake install PREFIX=${D}${prefix} CFG_DIR=${D}${sysconfdir} LCOV_PERL_PATH="/usr/bin/env perl"
}

BBCLASSEXTEND = "native nativesdk"
