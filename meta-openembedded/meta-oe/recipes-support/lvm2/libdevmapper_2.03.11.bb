# Break circular dependencies, only populate sysroot (header,
# libraries) to other recipe for compiling, recipe lvm2
# generates package libdevmapper
require lvm2.inc

DEPENDS += "autoconf-archive-native"

TARGET_CC_ARCH += "${LDFLAGS}"

do_install() {
    oe_runmake 'DESTDIR=${D}' -C libdm install
}

# Do not generate package libdevmapper
PACKAGES = ""

BBCLASSEXTEND = "native nativesdk"
