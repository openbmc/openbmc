DESCRIPTION = "The gSOAP toolkit provides a unique SOAP-to-C/C++ language binding \
for the development of SOAP Web Services and clients."
SECTION = "devel"
LICENSE = "GPL-2.0-with-OpenSSL-exception"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4f40a941379143186f9602242c3fb729 \
                    file://GPLv2_license.txt;md5=a33672dbe491b6517750a0389063508b"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}2/${BPN}_${PV}.zip \
           file://0001-Fix-out-of-tree-builds.patch \
           file://0001-stdgsoap2-Fix-build-with-musl.patch \
           "
SRC_URI[md5sum] = "212951d6e1435bb51fa4320f458809ea"
SRC_URI[sha256sum] = "3e7bb24a9e492f5cb86daca34054c9787152f1d7b70add36b789d03816d5ffa1"

inherit autotools

BBCLASSEXTEND = "native"

S = "${WORKDIR}/${BPN}-2.8"

PARALLEL_MAKE = ""

EXTRA_OEMAKE_class-target = "SOAP=${STAGING_BINDIR_NATIVE}/soapcpp2"

DEPENDS = "openssl zlib flex bison bison-native"
DEPENDS_append_class-target = " gsoap-native"

do_install_append() {
    install -d ${D}${libdir}
    for lib in libgsoapssl libgsoapssl++ libgsoap libgsoapck++ libgsoap++ libgsoapck
    do
        oe_libinstall -C gsoap $lib ${D}${libdir}
    done
}

do_install_class-native() {
    oe_runmake DESTDIR=${D} BINDIR=${D}${bindir} install
}

FILES_${PN} = "${bindir}/wsdl2h ${bindir}/soapcpp2"
FILES_${PN} += "${datadir}"
FILES_${PN}-staticdev = "${libdir}"
FILES_${PN}-dev = "${includedir}"
RRECOMMENDS_${PN}-dev = "${PN}-staticdev"
