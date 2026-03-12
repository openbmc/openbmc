DESCRIPTION = " \
    QCBOR is a powerful, commercial-quality CBOR encoder/decoder that \
    implements these RFCs: RFC8949, RFC7049, RFC8742, RFC8943 \
"

HOMEPAGE = "https://github.com/laurencelundblade/QCBOR"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9abe2371333f4ab0e62402a486f308a5"

SRC_URI = "git://github.com/laurencelundblade/QCBOR;protocol=https;branch=master;tag=v${PV}"

SRCREV = "590a23daf65af068cee81555c597063150e23539"


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

