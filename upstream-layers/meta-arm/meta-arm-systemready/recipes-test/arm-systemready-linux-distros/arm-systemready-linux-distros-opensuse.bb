require arm-systemready-linux-distros.inc

LICENSE = "AGPL-3.0-only & Apache-1.1 & Apache-2.0 & Artistic-1.0 & Artistic-2.0 \
           & BSD-2-Clause-Patent & BSD-2-Clause & BSD-3-Clause & BSD-4-Clause \
           & BSL-1.0 & CC-BY-3.0 & CC-BY-4.0 & CC-BY-SA-1.0 & CC-BY-SA-3.0 \
           & CC-BY-SA-4.0 & CC0-1.0 & CDDL-1.0 & GFDL-1.1-only \
           & GFDL-1.2-only & GFDL-1.3-only & GFDL-1.3-or-later \
           & GPL-1.0-or-later & GPL-2.0-only & GPL-2.0-or-later \
           & GPL-3.0-only & GPL-3.0-or-later & HPND & ICU & IPL-1.0 & IPA \
           & ISC & LGPL-2.0-only & LGPL-2.0-or-later & LGPL-2.1-only \
           & LGPL-2.1-or-later & LGPL-3.0-only & LGPL-3.0-or-later \
           & LPPL-1.3c & MIT & MPL-1.1 & MPL-2.0 & OFL-1.1 & OLDAP-2.8 \
           & OpenSSL & Python-2.0 & Sleepycat & Vim & W3C & Zlib"

LIC_FILES_CHKSUM = "\
file://${COMMON_LICENSE_DIR}/AGPL-3.0-only;md5=73f1eb20517c55bf9493b7dd6e480788 \
file://${COMMON_LICENSE_DIR}/Apache-1.1;md5=61cc638ff95ff4f38f243855bcec4317 \
file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10 \
file://${COMMON_LICENSE_DIR}/Artistic-1.0;md5=cda03bbdc3c1951996392b872397b798 \
file://${COMMON_LICENSE_DIR}/Artistic-2.0;md5=8bbc66f0ba93cec26ef526117e280266 \
file://${COMMON_LICENSE_DIR}/BSD-2-Clause-Patent;md5=0518d409dae93098cca8dfa932f3ab1b \
file://${COMMON_LICENSE_DIR}/BSD-2-Clause;md5=cb641bc04cda31daea161b1bc15da69f \
file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9 \
file://${COMMON_LICENSE_DIR}/BSD-4-Clause;md5=624d9e67e8ac41a78f6b6c2c55a83a2b \
file://${COMMON_LICENSE_DIR}/BSL-1.0;md5=65a7df9ad57aacf825fd252c4c33288c \
file://${COMMON_LICENSE_DIR}/CC-BY-3.0;md5=dfa02b5755629022e267f10b9c0a2ab7 \
file://${COMMON_LICENSE_DIR}/CC-BY-4.0;md5=9b33bbd06fb58995fb0e299cd38d1838 \
file://${COMMON_LICENSE_DIR}/CC-BY-SA-1.0;md5=681ffad43a0addd90f1bebf45675104e \
file://${COMMON_LICENSE_DIR}/CC-BY-SA-3.0;md5=3248afbd148270ac7337a6f3e2558be5 \
file://${COMMON_LICENSE_DIR}/CC-BY-SA-4.0;md5=4084714af41157e38872e798eb3fe1b1 \
file://${COMMON_LICENSE_DIR}/CC0-1.0;md5=0ceb3372c9595f0a8067e55da801e4a1 \
file://${COMMON_LICENSE_DIR}/CDDL-1.0;md5=d63dcc9297f2efd6c18d1e560b807bc6 \
file://${COMMON_LICENSE_DIR}/GFDL-1.1-only;md5=03322744a1a73f36ebf29f98cced39a4 \
file://${COMMON_LICENSE_DIR}/GFDL-1.2-only;md5=9f58808219e9a42ff1228309d6f83dc6 \
file://${COMMON_LICENSE_DIR}/GFDL-1.3-only;md5=e0771ae6a62dc8a2e50b1d450fea66b7 \
file://${COMMON_LICENSE_DIR}/GFDL-1.3-or-later;md5=e0771ae6a62dc8a2e50b1d450fea66b7 \
file://${COMMON_LICENSE_DIR}/GPL-1.0-or-later;md5=30c0b8a5048cc2f4be5ff15ef0d8cf61 \
file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6 \
file://${COMMON_LICENSE_DIR}/GPL-2.0-or-later;md5=fed54355545ffd980b814dab4a3b312c \
file://${COMMON_LICENSE_DIR}/GPL-3.0-only;md5=c79ff39f19dfec6d293b95dea7b07891 \
file://${COMMON_LICENSE_DIR}/GPL-3.0-or-later;md5=1c76c4cc354acaac30ed4d5eefea7245 \
file://${COMMON_LICENSE_DIR}/HPND;md5=faa364b3e3c6db0f74cc0e704ddf6b9c \
file://${COMMON_LICENSE_DIR}/ICU;md5=4d85ad1f393add71dc66bcf78e3ee584 \
file://${COMMON_LICENSE_DIR}/IPA;md5=17b18da2d8b2c43c598aa7583229ef1b \
file://${COMMON_LICENSE_DIR}/IPL-1.0;md5=be739b8845e6e98f99e206221fe9293b \
file://${COMMON_LICENSE_DIR}/ISC;md5=f3b90e78ea0cffb20bf5cca7947a896d \
file://${COMMON_LICENSE_DIR}/LGPL-2.0-only;md5=9427b8ccf5cf3df47c29110424c9641a \
file://${COMMON_LICENSE_DIR}/LGPL-2.0-or-later;md5=6d2d9952d88b50a51a5c73dc431d06c7 \
file://${COMMON_LICENSE_DIR}/LGPL-2.1-only;md5=1a6d268fd218675ffea8be556788b780 \
file://${COMMON_LICENSE_DIR}/LGPL-2.1-or-later;md5=2a4f4fd2128ea2f65047ee63fbca9f68 \
file://${COMMON_LICENSE_DIR}/LGPL-3.0-only;md5=bfccfe952269fff2b407dd11f2f3083b \
file://${COMMON_LICENSE_DIR}/LGPL-3.0-or-later;md5=c51d3eef3be114124d11349ca0d7e117 \
file://${COMMON_LICENSE_DIR}/LPPL-1.3c;md5=ba2fa6fe055623756de43a298d88a8b3 \
file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
file://${COMMON_LICENSE_DIR}/MPL-1.1;md5=1d38e87ed8d522c49f04e1efe0fab3ab \
file://${COMMON_LICENSE_DIR}/MPL-2.0;md5=815ca599c9df247a0c7f619bab123dad \
file://${COMMON_LICENSE_DIR}/OFL-1.1;md5=fac3a519e5e9eb96316656e0ca4f2b90 \
file://${COMMON_LICENSE_DIR}/OLDAP-2.8;md5=bb28ada4fbb5c3f52c233899b2e410a5 \
file://${COMMON_LICENSE_DIR}/OpenSSL;md5=4eb1764f3e65fafa1a25057f9082f2ae \
file://${COMMON_LICENSE_DIR}/Python-2.0;md5=a5c8025e305fb49e6d405769358851f6 \
file://${COMMON_LICENSE_DIR}/Sleepycat;md5=1cbb64231c94198653282f3ccab88ffb \
file://${COMMON_LICENSE_DIR}/Vim;md5=676d28582e2dca824e7e309a9865eeb1 \
file://${COMMON_LICENSE_DIR}/W3C;md5=4b1d0384b406508a63e51f7c69687700 \
file://${COMMON_LICENSE_DIR}/Zlib;md5=87f239f408daca8a157858e192597633 \
"

ARM_SYSTEMREADY_LINUX_DISTRO_INSTALL_SIZE = "6144"

PV = "15.5"
# possible value of ISO_TYPE: NET, DVD
ISO_TYPE = "DVD"
BUILD_NO = "491.1"
SRC_URI = "\
    https://download.opensuse.org/distribution/leap/${PV}/iso/openSUSE-Leap-${PV}-${ISO_TYPE}-aarch64-Build${BUILD_NO}-Media.iso;unpack=0;downloadfilename=${ISO_IMAGE_NAME}.iso;name=opensuse_iso_image \
    file://unattended-boot-conf/openSUSE/autoinst.xml \
    "
SRC_URI[opensuse_iso_image.sha256sum] = "456cc4f99b044429d8a89bd302c06e9e382d6ac4dc590139a7096ebb54f5357b"

TEST_SUITES = "${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "arm_systemready_opensuse_unattended", "", d)}"

ISO_LABEL = "${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "OEMDRV", "", d)}"
BOOT_CATALOG = "${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "boot.catalog", "", d)}"
BOOT_IMAGE = "${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "EFI/BOOT/bootaa64.efi", "", d)}"
EFI_IMAGE = "${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "boot/aarch64/efi", "", d)}"

modifyiso() {
    UNATTENDED_CONF_DIR="${UNPACKDIR}/unattended-boot-conf/openSUSE"

    #create installation configuration files, remove grub timeout, setup network
    cp "${UNATTENDED_CONF_DIR}/autoinst.xml" ${EXTRACTED_ISO_TEMP_DIR}
    sed -i 's/timeout=60/timeout=0/g' "${EXTRACTED_ISO_TEMP_DIR}/EFI/BOOT/grub.cfg"
}