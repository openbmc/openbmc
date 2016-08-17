SUMMARY = "X font aliases"

require xorg-font-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=bf0158b89be493d523d69d9f29265038 \
                    file://cyrillic/fonts.alias;md5=f40795b0640d6785826aecd3b16f6124 \
                    file://75dpi/fonts.alias;md5=6bc48023f2ae7f3bfc105db7b0ee6b49 \
                    file://misc/fonts.alias;md5=a8ec05d528431d4c9703b55a7efd67a8 \
                    file://100dpi/fonts.alias;md5=85bebd6ca213aa656c301a72eb4397cb"

SRC_URI += "file://nocompiler.patch"

DEPENDS = "util-macros-native font-util-native"
RDEPENDS_${PN} = "encodings font-util"
RDEPENDS_${PN}_class-native = "font-util-native"

inherit allarch

PE = "1"
PR = "${INC_PR}.3"

SRC_URI[md5sum] = "6d25f64796fef34b53b439c2e9efa562"
SRC_URI[sha256sum] = "8b453b2aae1cfa8090009ca037037b8c5e333550651d5a158b7264ce1d472c9a"
