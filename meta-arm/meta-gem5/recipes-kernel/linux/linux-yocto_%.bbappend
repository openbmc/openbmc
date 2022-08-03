FILESEXTRAPATHS:prepend:gem5-arm64 := "${THISDIR}:${THISDIR}/files:"

COMPATIBLE_MACHINE:gem5-arm64 = "gem5-arm64"
KMACHINE:gem5-arm64 = "gem5-arm64"
SRC_URI:append:gem5-arm64 = " file://gem5-kmeta;type=kmeta;name=gem5-kmeta;destsuffix=gem5-kmeta \
                              file://dts/gem5-arm64;subdir=add-files"

do_patch:append:gem5-arm64() {
    tar -C ${WORKDIR}/add-files/dts -cf - gem5-arm64 | \
        tar -C arch/arm64/boot/dts -xf -
}
