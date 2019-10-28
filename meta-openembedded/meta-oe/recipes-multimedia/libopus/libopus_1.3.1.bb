SUMMARY = "Opus Audio Codec"
DESCRIPTION = "The Opus codec is designed for interactive \
speech and audio transmission over the Internet. It is \
designed by the IETF Codec Working Group and incorporates \
technology from Skype's SILK codec and Xiph.Org's CELT codec."
HOMEPAGE = "http://www.opus-codec.org/"
SECTION = "libs/multimedia"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=e304cdf74c2a1b0a33a5084c128a23a3"

SRC_URI = "http://downloads.xiph.org/releases/opus/opus-${PV}.tar.gz"
SRC_URI[md5sum] = "d7c07db796d21c9cf1861e0c2b0c0617"
SRC_URI[sha256sum] = "65b58e1e25b2a114157014736a3d9dfeaad8d41be1c8179866f144a2fb44ff9d"

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
DEPENDS_append_aarch64 = " ne10"
DEPENDS_append_armv7a = " ne10"
DEPENDS_append_armv7ve = " ne10"

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
ARM_INSTRUCTION_SET_armv5 = "arm"

BBCLASSEXTEND = "native nativesdk"
