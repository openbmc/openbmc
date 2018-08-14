SUMMARY = "Lossless data compressor based on the LZMA algorithm"
HOMEPAGE = "http://lzip.nongnu.org/lzip.html"
SECTION = "console/utils"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=76d6e300ffd8fb9d18bd9b136a9bba13 \
                    file://decoder.cc;beginline=3;endline=16;md5=db09fe3f9573f94d0076f7f07959e6e1"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/lzip/lzip-${PV}.tar.gz"

SRC_URI[md5sum] = "4dd8790d7528440d034fc713a8680bd3"
SRC_URI[sha256sum] = "ffadc4f56be1bc0d3ae155ec4527bd003133bdc703a753b2cc683f610e646ba9"

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
