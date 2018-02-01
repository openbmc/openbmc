SUMMARY = "Protocol Buffers - structured data serialisation mechanism"
DESCRIPTION = "Protocol Buffers are a way of encoding structured data in an \
efficient yet extensible format. Google uses Protocol Buffers for almost \
all of its internal RPC protocols and file formats."
HOMEPAGE = "https://github.com/google/protobuf"
SECTION = "console/tools"
LICENSE = "BSD-3-Clause"

PACKAGE_BEFORE_PN = "${PN}-compiler"

DEPENDS = "zlib"
RDEPENDS_${PN}-compiler = "${PN}"
RDEPENDS_${PN}-dev += "${PN}-compiler"

LIC_FILES_CHKSUM = "file://LICENSE;md5=35953c752efc9299b184f91bef540095"

SRCREV = "a428e42072765993ff674fda72863c9f1aa2d268"

PV = "3.1.0+git${SRCPV}"

SRC_URI = "git://github.com/google/protobuf.git"

EXTRA_OECONF += " --with-protoc=echo"

inherit autotools

S = "${WORKDIR}/git"

FILES_${PN}-compiler = "${bindir} ${libdir}/libprotoc${SOLIBS}"

MIPS_INSTRUCTION_SET = "mips"

BBCLASSEXTEND = "native nativesdk"
