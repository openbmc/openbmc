DESCRIPTION = " \
    QCBOR is a powerful, commercial-quality CBOR encoder/decoder that \
    implements these RFCs: RFC8949, RFC7049, RFC8742, RFC8943 \
"

HOMEPAGE = "https://github.com/laurencelundblade/QCBOR"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://README.md;beginline=442;endline=463;md5=b55643261d6d221dac2b7a395105af62"

SRC_URI = "git://github.com/laurencelundblade/QCBOR;protocol=https;branch=master"

SRCREV = "44754f738c6534a4304a83d4c6e97b3d3193d887"

PV = "1.2+git"

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

