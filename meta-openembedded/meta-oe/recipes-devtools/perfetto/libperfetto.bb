LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2572d98547d43906b53615f856a8c2d"

require perfetto.inc

inherit meson

SRC_URI:append = " file://0001-meson-add-pc-file-for-lib_perfetto.patch"

LDFLAGS += "-Wl,--as-needed -latomic -Wl,--no-as-needed"

FILES:${PN} += "${datadir}"

BBCLASSEXTEND = "native nativesdk"
