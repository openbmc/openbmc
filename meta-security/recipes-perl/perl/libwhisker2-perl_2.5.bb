DESCRIPTION = "Libwhisker is a Perl module geared specificly for HTTP testing."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://LICENSE;md5=254b8e29606fce6d1c1a4c9e32354573"

SRC_URI = "http://sourceforge.net/projects/whisker/files/libwhisker/${PV}/libwhisker2-${PV}.tar.gz \
           file://libwhisker2.patch"

SRC_URI[md5sum] = "7cc1718dddde8f9a439d5622ae2f37eb"
SRC_URI[sha256sum] = "f45a1cf2ad2637b29dd1b13d7221ea12e3923ea09d107ced446400f19070a42f"

S = "${UNPACKDIR}/libwhisker2-2.5"

inherit cpan-base

PACKAGEGROUP ??=""
PACKAGEGROUP[ssl] = ", , libnet-ssleay-perl, libnet-ssleay-perl"

do_install() {
    perl_version="${@get_perl_version(d)}"
    install -d 755 ${D}${PERLLIBDIRS}/vendor_perl/${perl_version}
    install -d 755 ${D}${datadir}/perl/${perl_version}
    oe_runmake install DESTDIR=${D} INSTALLDIR=${PERLLIBDIRS}/vendor_perl/${perl_version} MANDIR=${datadir}/perl/${perl_version}
}

FILES:${PN} += "${datadir}/perl"

BBCLASSEXTEND = "native"
