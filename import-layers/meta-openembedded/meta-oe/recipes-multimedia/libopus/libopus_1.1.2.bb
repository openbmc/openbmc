SUMMARY = "Opus Audio Codec"
DESCRIPTION = "The Opus codec is designed for interactive \
speech and audio transmission over the Internet. It is \
designed by the IETF Codec Working Group and incorporates \
technology from Skype's SILK codec and Xiph.Org's CELT codec."
HOMEPAGE = "http://www.opus-codec.org/"
SECTION = "libs/multimedia"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=e304cdf74c2a1b0a33a5084c128a23a3"

SRC_URI = "http://downloads.xiph.org/releases/opus/opus-${PV}.tar.gz"
SRC_URI[md5sum] = "1f08a661bc72930187893a07f3741a91"
SRC_URI[sha256sum] = "0e290078e31211baa7b5886bcc8ab6bc048b9fc83882532da4a1a45e58e907fd"

S = "${WORKDIR}/opus-${PV}"

inherit autotools pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[fixed-point] = "--enable-fixed-point,,"
PACKAGECONFIG[float-approx] = "--enable-float-approx,,"

EXTRA_OECONF = "--with-NE10-includes=${STAGING_DIR_TARGET}${includedir} \
                --with-NE10-libraries=${STAGING_DIR_TARGET}${libdir} \
                --enable-asm \
                --enable-intrinsics \
               "

python () {
    if d.getVar('TARGET_FPU', True) in [ 'soft' ]:
        d.appendVar('PACKAGECONFIG', ' fixed-point')

    # Ne10 is only available for armv7 and aarch64
    if any((t.startswith('armv7') or t.startswith('aarch64')) for t in d.getVar('TUNE_FEATURES', True).split()):
        d.appendVar('DEPENDS', ' ne10')
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
ARM_INSTRUCTION_SET = "arm"
