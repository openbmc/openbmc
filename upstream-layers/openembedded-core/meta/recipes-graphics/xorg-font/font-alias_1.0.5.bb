SUMMARY = "X font aliases"

require xorg-font-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=bf0158b89be493d523d69d9f29265038 \
                    file://cyrillic/fonts.alias;md5=d27bc65a2655cacdbc2644b51c064c20 \
                    file://75dpi/fonts.alias;md5=6bc48023f2ae7f3bfc105db7b0ee6b49 \
                    file://misc/fonts.alias;md5=1bdafa7c31aa54f87f3531f2ef8ed5a6 \
                    file://100dpi/fonts.alias;md5=85bebd6ca213aa656c301a72eb4397cb \
                    "
SRC_URI_EXT = "xz"

DEPENDS = "util-macros-native font-util-native"
RDEPENDS:${PN} = "encodings font-util"
RDEPENDS:${PN}:class-native = "font-util-native"

inherit allarch

PE = "1"

SRC_URI[sha256sum] = "9f89e217bb73e0e3636a0a493fbf8b7c995156e0c53d9a0476d201b67c2d6b6e"
