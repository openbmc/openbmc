require lvm2.inc

SRC_URI[md5sum] = "153b7bb643eb26073274968e9026fa8f"
SRC_URI[sha256sum] = "b815a711a2fabaa5c3dc1a4a284df0268bf0f325f0fc0f5c9530c9bbb54b9964"

DEPENDS += "autoconf-archive-native"

TARGET_CC_ARCH += "${LDFLAGS}"

do_install() {
    oe_runmake 'DESTDIR=${D}' -C libdm install
}

RRECOMMENDS_${PN}_append_class-target = " lvm2-udevrules"

BBCLASSEXTEND = "native nativesdk"
