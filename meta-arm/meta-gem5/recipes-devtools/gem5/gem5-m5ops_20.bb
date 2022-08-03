require gem5-source_20.inc
inherit scons package

HOMEPAGE = "https://www.gem5.org/documentation/general_docs/m5ops"
SUMMARY = "m5ops provide pseudo-instructions to trigger gem5 functionality"
LICENSE = "BSD-3-Clause"

M5OPS_DIR = "util/m5"

SRC_URI += "file://0001-util-m5ops-optional-extra-build-flags.patch"

OUT_DIR = "build/${TARGET_ARCH}/out"

EXTRA_OESCONS += "${TARGET_ARCH}.CROSS_COMPILE=${TARGET_PREFIX} \
                  ${TARGET_ARCH}.CCFLAGS=--sysroot=${STAGING_DIR_TARGET} \
                  ${TARGET_ARCH}.LINKFLAGS=--sysroot=${STAGING_DIR_TARGET} \
                  -C ${S}/${M5OPS_DIR} ${OUT_DIR}/m5"

# The SConstruct file for m5ops does not provide a "install" target
# We do the install process within the recipe
do_install() {
    install -d ${D}${bindir} ${D}${libdir} ${D}${includedir}
    install -m 755 ${B}/${M5OPS_DIR}/${OUT_DIR}/m5 ${D}${bindir}
    install -m 644 ${B}/${M5OPS_DIR}/${OUT_DIR}/libm5.a ${D}${libdir}
    install -m 644 ${B}/include/gem5/m5ops.h ${D}${includedir}
}
