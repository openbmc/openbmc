DESCRIPTION="Protocol Buffers with small code size"
LICENSE="Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9db4b73a55a3994384112efcdb37c01f"

DEPENDS = "protobuf-native"

SRC_URI = "git://github.com/nanopb/nanopb.git;branch=master;protocol=https \
    file://0001-CMakeLists.txt-allow-to-set-PYTHON_INSTDIR-from-outs.patch \
"
SRCREV = "b97aa657a706d3ba4a9a6ccca7043c9d6fe41cba"

S = "${WORKDIR}/git"

inherit cmake python3native

EXTRA_OECMAKE += "-DPYTHON_INSTDIR=${PYTHON_SITEPACKAGES_DIR}"

do_install:append() {
    install -Dm 0755 ${S}/generator/nanopb_generator.py ${D}${bindir}/nanopb_generator.py
    install -Dm 0755 ${S}/generator/protoc-gen-nanopb ${D}${bindir}/protoc-gen-nanopb
    install -Dm 0755 ${S}/generator/proto/__init__.py ${D}${PYTHON_SITEPACKAGES_DIR}/proto/__init__.py
}

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR}"
FILES:${PN}-dev += "${libdir}/cmake/${BPN}"

RDEPENDS:${PN} += "\
   ${PYTHON_PN}-protobuf \
   protobuf-compiler \
"

BBCLASSEXTEND = "native nativesdk"

