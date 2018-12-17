SUMMARY = "Lossless data compressor based on the LZMA algorithm"
HOMEPAGE = "http://lzip.nongnu.org/lzip.html"
SECTION = "console/utils"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=76d6e300ffd8fb9d18bd9b136a9bba13 \
                    file://decoder.cc;beginline=3;endline=16;md5=db09fe3f9573f94d0076f7f07959e6e1"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/lzip/lzip-${PV}.tar.gz"

SRC_URI[md5sum] = "21c1517da2ace9689fd953918660b3c3"
SRC_URI[sha256sum] = "c93b81a5a7788ef5812423d311345ba5d3bd4f5ebf1f693911e3a13553c1290c"

CONFIGUREOPTS = "\
    '--srcdir=${S}' \
    '--prefix=${prefix}' \
    '--exec-prefix=${exec_prefix}' \
    '--bindir=${bindir}' \
    '--datadir=${datadir}' \
    '--infodir=${infodir}' \
    '--sysconfdir=${sysconfdir}' \
    'CXX=${CXX}' \
    'CPPFLAGS=${CPPFLAGS}' \
    'CXXFLAGS=${CXXFLAGS}' \
    'LDFLAGS=${LDFLAGS}' \
"
EXTRA_OEMAKE = ""

B = "${S}/obj"
do_configure () {
    ${S}/configure ${CONFIGUREOPTS}
}

do_install () {
    oe_runmake 'DESTDIR=${D}' install
    # Info dir listing isn't interesting at this point so remove it if it exists.
    if [ -e "${D}${infodir}/dir" ]; then
        rm -f ${D}${infodir}/dir
    fi
}

BBCLASSEXTEND = "native nativesdk"
