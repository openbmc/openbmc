require lvm2.inc

SRC_URI[md5sum] = "bc26da66e96727babbd288bb0f050339"
SRC_URI[sha256sum] = "24997e26dfc916151707c9da504d38d0473bec3481a8230b676bc079041bead6"

DEPENDS += "autoconf-archive-native"

TARGET_CC_ARCH += "${LDFLAGS}"

do_install() {
    oe_runmake 'DESTDIR=${D}' -C libdm install
    oe_runmake 'DESTDIR=${D}' -C tools install_device-mapper
}

RRECOMMENDS_${PN}_append_class-target = " lvm2-udevrules"

BBCLASSEXTEND = "native nativesdk"
