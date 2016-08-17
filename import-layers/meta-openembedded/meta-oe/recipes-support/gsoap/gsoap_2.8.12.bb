DESCRIPTION = "The gSOAP toolkit provides a unique SOAP-to-C/C++ language binding \
for the development of SOAP Web Services and clients."
SECTION = "devel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b338b08b1b61e028e0f399a4de25e58f"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}2/${BPN}_${PV}.zip"
SRC_URI[md5sum] = "5700d26fc6fe3073d038349e19c3640d"
SRC_URI[sha256sum] = "51eef118544fa846f4d2dea2eedf91c84c46a1abeafc5eee3dcff783f4015a00"

inherit autotools

BBCLASSEXTEND = "native"

PR = "r1"

S = "${WORKDIR}/${BPN}-2.8"

PARALLEL_MAKE = ""

EXTRA_OEMAKE_class-target = "SOAP=${STAGING_BINDIR_NATIVE}/soapcpp2"

DEPENDS = "openssl zlib flex bison"
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
