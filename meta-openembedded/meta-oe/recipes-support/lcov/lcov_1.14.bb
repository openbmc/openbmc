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
    gcov-symlinks \
    libjson-perl \
    libperlio-gzip-perl \
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

SRC_URI = " \
           http://downloads.sourceforge.net/ltp/${BP}.tar.gz \
           file://0001-geninfo-Add-intermediate-text-format-support.patch \
           file://0002-geninfo-Add-intermediate-JSON-format-support.patch \
	   "

SRC_URI[md5sum] = "0220d01753469f83921f8f41ae5054c1"
SRC_URI[sha256sum] = "14995699187440e0ae4da57fe3a64adc0a3c5cf14feab971f8db38fb7d8f071a"

do_install() {
    oe_runmake install PREFIX=${D}${prefix} CFG_DIR=${D}${sysconfdir}
}

BBCLASSEXTEND = "native nativesdk"
