SUMMARY = "Opus Audio Codec"
DESCRIPTION = "The Opus codec is designed for interactive \
speech and audio transmission over the Internet. It is \
designed by the IETF Codec Working Group and incorporates \
technology from Skype's SILK codec and Xiph.Org's CELT codec."
HOMEPAGE = "http://www.opus-codec.org/"
SECTION = "libs/multimedia"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=4b365c2155d66e550e1447075d6744a5"

SRC_URI = "http://downloads.xiph.org/releases/opus/opus-${PV}.tar.gz"
SRC_URI[sha256sum] = "65c1d2f78b9f2fb20082c38cbe47c951ad5839345876e46941612ee87f9a7ce1"

S = "${WORKDIR}/opus-${PV}"

inherit autotools pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[fixed-point] = "--enable-fixed-point,,"
PACKAGECONFIG[float-approx] = "--enable-float-approx,,"

EXTRA_OECONF = " \
    --with-NE10-includes=${STAGING_DIR_TARGET}${includedir} \
    --with-NE10-libraries=${STAGING_DIR_TARGET}${libdir} \
    --enable-asm \
    --enable-intrinsics \
    --enable-custom-modes \
"

# ne10 is available only for armv7a, armv7ve and aarch64
DEPENDS:append:aarch64 = " ne10"
DEPENDS:append:armv7a = "${@bb.utils.contains("TUNE_FEATURES","neon"," ne10","",d)}"
DEPENDS:append:armv7ve = "${@bb.utils.contains("TUNE_FEATURES","neon"," ne10","",d)}"

python () {
    if d.getVar('TARGET_FPU') in [ 'soft' ]:
        d.appendVar('PACKAGECONFIG', ' fixed-point')
}

# Fails to build with thumb-1 (qemuarm)
#| {standard input}: Assembler messages:
#| {standard input}:389: Error: selected processor does not support Thumb mode `smull r5,r7,r1,r4'
#| {standard input}:418: Error: selected processor does not support Thumb mode `smull r5,r6,r4,r1'
#| {standard input}:448: Error: selected processor does not support Thumb mode `smull r4,r5,r1,r0'
#| {standard input}:474: Error: selected processor does not support Thumb mode `smull r0,r4,r8,r1'
#| {standard input}:510: Error: selected processor does not support Thumb mode `smull fp,r0,r10,r1'
#| {standard input}:553: Error: selected processor does not support Thumb mode `smull fp,r1,r10,r3'
#| {standard input}:741: Error: selected processor does not support Thumb mode `smull r3,r0,r6,r10'
#| {standard input}:761: Error: selected processor does not support Thumb mode `smull fp,r2,r3,r9'
#| {standard input}:773: Error: selected processor does not support Thumb mode `smull fp,r3,r5,r8'
#| make[2]: *** [celt/celt.lo] Error 1
ARM_INSTRUCTION_SET:armv5 = "arm"

BBCLASSEXTEND = "native nativesdk"

CVE_PRODUCT += "opus-codec:opus"
