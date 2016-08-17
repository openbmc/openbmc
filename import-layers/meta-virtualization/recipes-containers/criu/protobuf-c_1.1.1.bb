SUMMARY = "protobuf-c"
DESCRIPTION = "This package provides a code generator and runtime libraries to use Protocol Buffers from pure C"
HOMEPAGE = "http://code.google.com/p/protobuf-c/"
SECTION = "console/tools"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://protobuf-c/protobuf-c.c;endline=28;md5=0feb44cc63eacef97219b0174967492f"

COMPATIBLE_HOST = "(x86_64|arm|aarch64).*-linux"

DEPENDS = "protobuf protobuf-c-native"

SRC_URI[md5sum] = "41d437677ea16f9d3611d98841c4af3b"
SRC_URI[sha256sum] = "09c5bb187b7a8e86bc0ff860f7df86370be9e8661cdb99c1072dcdab0763562c"
SRC_URI = "https://github.com/protobuf-c/protobuf-c/releases/download/v1.1.1/protobuf-c-1.1.1.tar.gz "
SRC_URI_append_class-target ="file://0001-protobuf-c-Remove-the-rules-which-depend-on-the-nati.patch"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"

do_configure_prepend_class-target() {
    export PKG_CONFIG_PATH="${STAGING_LIBDIR_NATIVE}/pkgconfig:${PKG_CONFIG_PATH}"
}

do_install_append_class-native() {
    install -m 755 ${B}/t/generated-code2/cxx-generate-packed-data ${D}/${bindir}
}
