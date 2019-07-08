SUMMARY = "A graphical front-end for gcov"
HOMEPAGE = "http://ltp.sourceforge.net/coverage/lcov.php"
DESCRIPTION = "LCOV is a graphical front-end for GCC's coverage testing \
tool gcov. It collects gcov data for multiple source files and creates \
HTML pages containing the source code annotated with coverage information. \
It also adds overview pages for easy navigation within the file structure. \
LCOV supports statement, function and branch coverage measurement."
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

RDEPENDS_${PN} += " \
    gcov \
    perl \
    perl-module-filehandle \
    perl-module-getopt-std \
    perl-module-digest-sha \
    perl-module-constant \
    perl-module-cwd \
    perl-module-errno \
    perl-module-file-basename \
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

SRC_URI = "http://downloads.sourceforge.net/ltp/${BP}.tar.gz"

SRC_URI[md5sum] = "e79b799ae3ce149aa924c7520e993024"
SRC_URI[sha256sum] = "c282de8d678ecbfda32ce4b5c85fc02f77c2a39a062f068bd8e774d29ddc9bf8"

do_install() {
    oe_runmake install PREFIX=${D}
}

