DESCRIPTION="Protocol Buffers with small code size"
LICENSE="Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9db4b73a55a3994384112efcdb37c01f"

DEPENDS = "protobuf-native"

SRC_URI = "git://github.com/nanopb/nanopb.git;branch=master;protocol=https"
SRCREV = "70f0de9877b1ce12abc0229d5df84db6349fcbfc"

S = "${WORKDIR}/git"

inherit cmake python3native

do_install_append() {
    install -Dm 0755 ${S}/generator/nanopb_generator.py ${D}${bindir}/nanopb_generator.py
    install -Dm 0755 ${S}/generator/protoc-gen-nanopb ${D}${bindir}/protoc-gen-nanopb
    install -Dm 0755 ${S}/generator/proto/__init__.py ${D}${PYTHON_SITEPACKAGES_DIR}/proto/__init__.py
}

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR}"
FILES_${PN}-dev += "${libdir}/cmake/${BPN}"

RDEPENDS_${PN} += "\
   ${PYTHON_PN}-protobuf \
   protobuf-compiler \
"

BBCLASSEXTEND = "native nativesdk"

PNBLACKLIST[nanopb] = "Needs forward porting to use python3"
