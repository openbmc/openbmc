SUMMARY = "protobuf-c"
DESCRIPTION = "This package provides a code generator and runtime libraries to use Protocol Buffers from pure C"
HOMEPAGE = "http://code.google.com/p/protobuf-c/"
SECTION = "console/tools"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=235c3195a3968524dc1524b4ebea0c0e"

COMPATIBLE_HOST = "(x86_64|arm|aarch64).*-linux"

DEPENDS = "protobuf protobuf-c-native"

SRC_URI[md5sum] = "e544249c329391fff512c3874895cfbe"
SRC_URI[sha256sum] = "846eb4846f19598affdc349d817a8c4c0c68fd940303e6934725c889f16f00bd"
SRC_URI = "https://github.com/protobuf-c/protobuf-c/releases/download/v1.2.1/protobuf-c-1.2.1.tar.gz "
#SRC_URI_append_class-target ="file://0001-protobuf-c-Remove-the-rules-which-depend-on-the-nati.patch"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"

do_configure_prepend_class-target() {
    export PKG_CONFIG_PATH="${STAGING_LIBDIR_NATIVE}/pkgconfig:${PKG_CONFIG_PATH}"
}

do_install_append_class-native() {
    install -m 755 ${B}/t/generated-code2/cxx-generate-packed-data ${D}/${bindir}
}
