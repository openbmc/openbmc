LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c0c9924c5c63b4834b8b1959816c8e3b"

require perfetto.inc

inherit meson

SRC_URI:append = " file://0001-meson-add-pc-file-for-lib_perfetto.patch"

LDFLAGS += "-Wl,--as-needed -latomic -Wl,--no-as-needed"

FILES:${PN} += "${datadir}"

BBCLASSEXTEND = "native nativesdk"
