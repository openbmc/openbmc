DESCRIPTION = " \
    QCBOR is a powerful, commercial-quality CBOR encoder/decoder that \
    implements these RFCs: RFC8949, RFC7049, RFC8742, RFC8943 \
"

HOMEPAGE = "https://github.com/laurencelundblade/QCBOR"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=498c298542365dfcfe29948c72a5849b"

SRC_URI = "git://github.com/laurencelundblade/QCBOR;protocol=https;branch=master"

SRCREV = "4487f10e1bf258434fb8a39e4f59c29e31910ad0"

S = "${WORKDIR}/git"

inherit pkgconfig

CFLAGS += " \
    -DUSEFULBUF_DISABLE_ALL_FLOAT \
"

do_install(){
    install -d ${D}${libdir}
    install -m 755 ${S}/libqcbor.a ${D}${libdir}/
    install -d ${D}${includedir}/qcbor
    install -m 644 ${S}/inc/*.h ${D}${includedir}
    install -m 644 ${S}/inc/qcbor/*.h ${D}${includedir}/qcbor
}

