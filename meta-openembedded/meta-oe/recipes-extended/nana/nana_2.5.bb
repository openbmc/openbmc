SUMMARY = "Support for assertion checking and logging in GNU C/C++"
DESCRIPTION = "GNU Nana is a free library providing improved support for assertion\
checking (as in assert.h) and logging (printf style debugging) in \
GNU C and C++."
SECTION = "Development/Languages/C and C++"

SRC_URI = "http://download.savannah.gnu.org/releases/${BPN}/${BP}.tar.gz \
    file://change-mandir-to-DESTDIR.patch \
    file://modify-acinclude.m4-and-configure.in.patch \
"
SRC_URI[md5sum] = "66c88aa0ad095b2e67673773135475f1"
SRC_URI[sha256sum] = "fd1819ffea94b209513959447e4802afe2719600e7d161cd78b265a42812affa"

LICENSE = "BSD-2-Clause"

LIC_FILES_CHKSUM = "file://COPYING;md5=16aa57f3b7fdda870cee597275bd5d11"

inherit autotools-brokensep pkgconfig texinfo

EXTRA_OEMAKE = "DESTDIR=${D}"

do_configure_prepend_class-nativesdk() {
    sed -i -e 's:@CPP@:\$\{CXX\} \$\{CXXFLAGS\} \-E:g' ${S}/src/nana.in
    sed -i -e 's:@CC@:\$\{CC\} \$\{CFLAGS\} \-E:g' ${S}/src/nana-clg.in
    sed -i -e 's:@CXX@::g' ${S}/src/nana-c++lg.in
    sed -i -e 's:@GDB@:\$\{GDB\}:g' ${S}/src/nana-run.in
}

do_install_prepend() {
    install -d ${D}${mandir}/man1
    install -d ${D}${mandir}/man3
    install -d ${D}${datadir}/info
}
BBCLASSEXTEND = "native nativesdk"
