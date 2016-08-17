SUMMARY = "Perl module that compare 8-bit scalar data according to the current locale"
DESCRIPTION = "This module provides you with objects that will collate according to \
your national character set, provided that the POSIX setlocale() function is supported \
on your system."

HOMEPAGE = "http://search.cpan.org/~flora/I18N-Collate/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://LICENSE;md5=ff6d629144a6ec1ea8c300f75760184f"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/F/FL/FLORA/I18N-Collate-${PV}.tar.gz"

SRC_URI[md5sum] = "72ddb6d1c59cfdf31aa3b04799b86af0"
SRC_URI[sha256sum] = "9174506bc903eda89690394e3f45558ab7e013114227896d8569d6164648fe37"

S = "${WORKDIR}/I18N-Collate-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
