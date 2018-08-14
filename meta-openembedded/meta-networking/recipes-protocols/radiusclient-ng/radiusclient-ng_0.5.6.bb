SUMMARY = "RADIUS protocol client library"
DESCRIPTION = "Portable, easy-to-use and standard compliant library suitable \
for developing free and commercial software that need support for a RADIUS \
protocol (RFCs 2128 and 2139)."
HOMEPAGE = "http://sourceforge.net/projects/radiusclient-ng.berlios/"
SECTION = "net"

SRC_URI = "${DEBIAN_MIRROR}/main/r/${BPN}/${BPN}_${PV}.orig.tar.gz \
   file://Modify-configure.in-and-etc-Makefile.am.patch \
   file://config-site.radiusclient-ng-${PV}"

SRC_URI[md5sum] = "6fb7d4d0aefafaee7385831ac46a8e9c"
SRC_URI[sha256sum] = "282a9f1355f190efbb06c0d7c4e062eaa652caf342ed3ad361ac595f72f09f14"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=3e47566c9271b786693d8a08792dbf41"

inherit autotools-brokensep

EXTRA_OECONF += "--disable-static"

do_configure_prepend () {
    export CONFIG_SITE=./config-site.${P}
}

do_compile_prepend() {
    for m in `find . -name "Makefile"` ; do
        sed -i -e 's:^program_transform_name =.*:program_transform_name =:g' ${m}
    done
}

do_install() {
    oe_runmake DESTDIR=${D} install
    rm -f ${D}${libdir}/*.la
    rm -f ${D}${sbindir}/radexample
}
