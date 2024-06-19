SUMMARY = "Arm SystemReady IR ACS"
DESCRIPTION = "Arm SystemReady IR Architecture Compliance Suite prebuilt image"

LICENSE = "AFL-2.1 & Apache-2.0 & BSD-2-Clause & BSD-2-Clause-Patent \
           & BSD-3-Clause & BSD-4-Clause & bzip2-1.0.4 & bzip2-1.0.6 & CC-BY-SA-4.0 \
           & curl & GPL-2.0-only & GPL-2.0-or-later & GPL-3.0-only \
           & GPL-3.0-or-later & GPL-3.0-with-GCC-exception & ISC \
           & LGPL-2.0-only & LGPL-2.0-or-later & LGPL-2.1-only \
           & LGPL-2.1-or-later & LGPL-3.0-only & MIT & MPL-2.0 & PD & PSF-2.0 \
           & Python-2.0 & Unicode-DFS-2016 & Unicode-TOU & Zlib"
LIC_FILES_CHKSUM = "\
file://${COMMON_LICENSE_DIR}/AFL-2.1;md5=e40039b90e182a056bcd9ad3e47ddd71 \
file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10 \
file://${COMMON_LICENSE_DIR}/BSD-2-Clause;md5=cb641bc04cda31daea161b1bc15da69f \
file://${COMMON_LICENSE_DIR}/BSD-2-Clause-Patent;md5=0518d409dae93098cca8dfa932f3ab1b \
file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9 \
file://${COMMON_LICENSE_DIR}/BSD-4-Clause;md5=624d9e67e8ac41a78f6b6c2c55a83a2b \
file://${COMMON_LICENSE_DIR}/bzip2-1.0.4;md5=452e1b423688222dcfc3cb9462c92902 \
file://${COMMON_LICENSE_DIR}/bzip2-1.0.6;md5=841c5495611ae95b13e80fa4a0627333 \
file://${COMMON_LICENSE_DIR}/CC-BY-SA-4.0;md5=4084714af41157e38872e798eb3fe1b1 \
file://${COMMON_LICENSE_DIR}/curl;md5=f7adb1397db248527ffed14d947e445c \
file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6 \
file://${COMMON_LICENSE_DIR}/GPL-2.0-or-later;md5=fed54355545ffd980b814dab4a3b312c \
file://${COMMON_LICENSE_DIR}/GPL-3.0-only;md5=c79ff39f19dfec6d293b95dea7b07891 \
file://${COMMON_LICENSE_DIR}/GPL-3.0-or-later;md5=1c76c4cc354acaac30ed4d5eefea7245 \
file://${COMMON_LICENSE_DIR}/GPL-3.0-with-GCC-exception;md5=aef5f35c9272f508be848cd99e0151df \
file://${COMMON_LICENSE_DIR}/ISC;md5=f3b90e78ea0cffb20bf5cca7947a896d \
file://${COMMON_LICENSE_DIR}/LGPL-2.0-only;md5=9427b8ccf5cf3df47c29110424c9641a \
file://${COMMON_LICENSE_DIR}/LGPL-2.0-or-later;md5=6d2d9952d88b50a51a5c73dc431d06c7 \
file://${COMMON_LICENSE_DIR}/LGPL-2.1-only;md5=1a6d268fd218675ffea8be556788b780 \
file://${COMMON_LICENSE_DIR}/LGPL-2.1-or-later;md5=2a4f4fd2128ea2f65047ee63fbca9f68 \
file://${COMMON_LICENSE_DIR}/LGPL-3.0-only;md5=bfccfe952269fff2b407dd11f2f3083b \
file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
file://${COMMON_LICENSE_DIR}/MPL-2.0;md5=815ca599c9df247a0c7f619bab123dad \
file://${COMMON_LICENSE_DIR}/PD;md5=b3597d12946881e13cb3b548d1173851 \
file://${COMMON_LICENSE_DIR}/PSF-2.0;md5=76c1502273262a5ebefb50dfb20d7c4f \
file://${COMMON_LICENSE_DIR}/Python-2.0;md5=a5c8025e305fb49e6d405769358851f6 \
file://${COMMON_LICENSE_DIR}/Unicode-DFS-2016;md5=907371994d651afe53e98adc27824669 \
file://${COMMON_LICENSE_DIR}/Unicode-TOU;md5=666362dc5dba74f477af0f44fb85bd22 \
file://${COMMON_LICENSE_DIR}/Zlib;md5=87f239f408daca8a157858e192597633 \
"
IMAGE_CLASSES:remove = "license_image"

COMPATIBLE_MACHINE = "(fvp-.+|.+-fvp)"

TEST_SUITES = "arm_systemready_ir_acs"

PV = "2.0.0"
PV_DATE = "23.03"
FULL_PV = "v${PV_DATE}_${PV}"
ARM_SYSTEMREADY_IR_ACS_BRANCH ?= "main"
IMAGE_FILENAME = "ir-acs-live-image-generic-arm64.wic"
SRC_URI = " \
    https://github.com/ARM-software/arm-systemready/raw/${ARM_SYSTEMREADY_IR_ACS_BRANCH}/IR/prebuilt_images/${FULL_PV}/${IMAGE_FILENAME}.xz;name=acs-img \
    git://git.gitlab.arm.com/systemready/systemready-ir-template.git;protocol=https;nobranch=1;destsuffix=systemready-ir-template;name=sr-ir-template \
"
SRC_URI[acs-img.sha256sum] = "ea52f84dab44bde97de3e2d2224d883acaae35724dd8e2bdfb125de49040f9b3"
# Revision pointing to v2023.04 tag
SRCREV_sr-ir-template = "c714db178ddf72e5ae5017f15421095297d5bf0e"

inherit arm-systemready-acs
