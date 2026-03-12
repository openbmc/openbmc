DESCRIPTION = "The gSOAP toolkit provides a unique SOAP-to-C/C++ language binding \
for the development of SOAP Web Services and clients."
SECTION = "devel"
LICENSE = "GPL-2.0-with-OpenSSL-exception"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7eaf5d4df67c0db38549ba3c714a37d6 \
                    file://GPLv2_license.txt;md5=a33672dbe491b6517750a0389063508b"

SRC_URI = "https://prdownloads.sourceforge.net/${BPN}2/${BPN}_${PV}.zip \
           file://0001-Provide-strtod_l-signature-on-non-glibc-linux-musl-s.patch"
SRC_URI[sha256sum] = "a6c9fb9088efd60ad0e983fa83d8440fe128514db22297b5b3f2de302106e55c"

inherit autotools

BBCLASSEXTEND = "native"

S = "${UNPACKDIR}/${BPN}-2.8"

EXTRA_OECONF += "--enable-ipv6 --enable-c-locale"
PARALLEL_MAKE = ""

EXTRA_OEMAKE:class-target = "SOAP=${STAGING_BINDIR_NATIVE}/soapcpp2"

DEPENDS = "openssl zlib flex bison bison-native"
DEPENDS:append:class-target = " gsoap-native"

do_install:append() {
    install -d ${D}${libdir}
    for lib in libgsoapssl libgsoapssl++ libgsoap libgsoapck++ libgsoap++ libgsoapck
    do
        oe_libinstall -C gsoap $lib ${D}${libdir}
    done
}

do_install:class-native() {
    oe_runmake DESTDIR=${D} BINDIR=${D}${bindir} install
}

FILES:${PN} = "${bindir}/wsdl2h ${bindir}/soapcpp2"
FILES:${PN} += "${datadir}"
FILES:${PN}-staticdev = "${libdir}"
FILES:${PN}-dev = "${includedir}"
RRECOMMENDS:${PN}-dev = "${PN}-staticdev"
