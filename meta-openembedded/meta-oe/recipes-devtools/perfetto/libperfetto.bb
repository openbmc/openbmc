LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f87516e0b698007e9e75a1fe1012b390"

require perfetto.inc

inherit meson

SRC_URI:append = " file://0001-meson-add-pc-file-for-lib_perfetto.patch"

LDFLAGS += "-Wl,--as-needed -latomic -Wl,--no-as-needed"

FILES:${PN} += "${datadir}"

BBCLASSEXTEND = "native nativesdk"
