require arm-systemready-linux-distros.inc

# The Fedora project does not provide a license manifest for the distributed ISO images.
# The following list only contains the SPDX license identifiers found on the rpm
# packages from the ISO image and is not exhaustive.
# For more information about Fedora licenses, including the non-free ones, refer to
# https://docs.fedoraproject.org/en-US/legal/fedora-linux-license/.
LICENSE = "GPL-1.0-only & GPL-1.0-or-later & GPL-2.0-only &  GPL-2.0-or-later & GPL-2.0-with-font-exception \
           & GPL-2.0-with-GCC-exception & GPL-3.0-only & GPL-3.0-or-later & GPL-3-with-bison-exception \
           & LGPL-2.0-only & LGPL-2.0-or-later & LGPL-2.1-only & LGPL-2.1-or-later & LGPL-3.0-only & LGPL-3.0-or-later \
           & 0BSD & BSD-2-Clause & BSD-3-Clause & BSD-3-Clause-Modification & BSD-4-Clause & BSD-4-Clause-UC \
           & ClArtistic & Artistic-2.0 & Artistic-1.0-Perl & Apache-2.0 & Apache-2.0-with-LLVM-exception & Zlib \
           & zlib-acknowledgement & Sleepycat & MIT & MIT-open-group & MIT-Modern-Variant & Unlicense & ISC \
           & AFL-2.1 & AGPL-3.0-only & AGPL-3.0-or-later & FSFAP & MPL-1.1 & MPL-2.0 & CC-BY-3.0 & CC-BY-4.0 \ 
           & CC-BY-SA-4.0 & CC0-1.0 & NCSA & APSL-2.0 & IJG & psutils & Sendmail & blessing & NTP & BSL-1.0 \
           & GFDL-1.1-or-later & GFDL-1.2-or-later & GFDL-1.3 & GFDL-1.3-or-later & GFDL-1.3-no-invariants-or-later \
           & NPL-1.1 & libtiff & Vim & curl & EUPL-1.1 & OFL-1.1 & OFL-1.1-RFN & FTL & Info-ZIP & Interbase-1.0 \
           & Unicode-DFS-2016 & SISSL & LPPL-1.3a & SGI-B-2.0 & PSF-2.0 & X11 & OLDAP-2.8 & PostgreSQL & OPUBL-1.0"

LIC_FILES_CHKSUM = "\
file://${COMMON_LICENSE_DIR}/GPL-1.0-only;md5=e9e36a9de734199567a4d769498f743d \
file://${COMMON_LICENSE_DIR}/GPL-1.0-or-later;md5=30c0b8a5048cc2f4be5ff15ef0d8cf61 \
file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6 \
file://${COMMON_LICENSE_DIR}/GPL-2.0-or-later;md5=fed54355545ffd980b814dab4a3b312c \
file://${COMMON_LICENSE_DIR}/GPL-2.0-with-font-exception;md5=bf93e21a513f6f923474e62fb920434d \
file://${COMMON_LICENSE_DIR}/GPL-2.0-with-GCC-exception;md5=14c42911132e8c9008911385aede6449 \
file://${COMMON_LICENSE_DIR}/GPL-3.0-only;md5=c79ff39f19dfec6d293b95dea7b07891 \
file://${COMMON_LICENSE_DIR}/GPL-3.0-or-later;md5=1c76c4cc354acaac30ed4d5eefea7245 \
file://${COMMON_LICENSE_DIR}/GPL-3-with-bison-exception;md5=6e1bac3dc21fcc4fa049cf5c407eb7a2 \
file://${COMMON_LICENSE_DIR}/LGPL-2.0-only;md5=9427b8ccf5cf3df47c29110424c9641a \
file://${COMMON_LICENSE_DIR}/LGPL-2.0-or-later;md5=6d2d9952d88b50a51a5c73dc431d06c7 \
file://${COMMON_LICENSE_DIR}/LGPL-2.1-only;md5=1a6d268fd218675ffea8be556788b780 \
file://${COMMON_LICENSE_DIR}/LGPL-2.1-or-later;md5=2a4f4fd2128ea2f65047ee63fbca9f68 \
file://${COMMON_LICENSE_DIR}/LGPL-3.0-only;md5=bfccfe952269fff2b407dd11f2f3083b \
file://${COMMON_LICENSE_DIR}/LGPL-3.0-or-later;md5=c51d3eef3be114124d11349ca0d7e117 \
file://${COMMON_LICENSE_DIR}/0BSD;md5=f667a3c3830a55a17ec3067709f4526c \
file://${COMMON_LICENSE_DIR}/BSD-2-Clause;md5=cb641bc04cda31daea161b1bc15da69f \
file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9 \
file://${COMMON_LICENSE_DIR}/BSD-3-Clause-Modification;md5=27b46022df7bdef61a1e404fc3573f83 \
file://${COMMON_LICENSE_DIR}/BSD-4-Clause;md5=624d9e67e8ac41a78f6b6c2c55a83a2b \
file://${COMMON_LICENSE_DIR}/BSD-4-Clause-UC;md5=1da3cf8ad50cd8d5d1de3cfc53196d01 \
file://${COMMON_LICENSE_DIR}/ClArtistic;md5=f633bbf0697ec33066b83adfa9ebe51d \
file://${COMMON_LICENSE_DIR}/Artistic-2.0;md5=8bbc66f0ba93cec26ef526117e280266 \
file://${COMMON_LICENSE_DIR}/Artistic-1.0-Perl;md5=8feedd169dbd5738981843bd7d931f9f \
file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10 \
file://${COMMON_LICENSE_DIR}/Apache-2.0-with-LLVM-exception;md5=0bcd48c3bdfef0c9d9fd17726e4b7dab \
file://${COMMON_LICENSE_DIR}/Zlib;md5=87f239f408daca8a157858e192597633 \
file://${COMMON_LICENSE_DIR}/zlib-acknowledgement;md5=c76c64e2cf99efcfb5e2b26aa86b12e6 \
file://${COMMON_LICENSE_DIR}/Sleepycat;md5=1cbb64231c94198653282f3ccab88ffb \
file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
file://${COMMON_LICENSE_DIR}/MIT-open-group;md5=a7c50bba311e4b09d48a526eedcef837 \
file://${COMMON_LICENSE_DIR}/MIT-Modern-Variant;md5=272dea2b67586002978254bc04648ab2 \
file://${COMMON_LICENSE_DIR}/Unlicense;md5=7246f848faa4e9c9fc0ea91122d6e680 \
file://${COMMON_LICENSE_DIR}/ISC;md5=f3b90e78ea0cffb20bf5cca7947a896d \
file://${COMMON_LICENSE_DIR}/AFL-2.1;md5=e40039b90e182a056bcd9ad3e47ddd71 \
file://${COMMON_LICENSE_DIR}/AGPL-3.0-only;md5=73f1eb20517c55bf9493b7dd6e480788 \
file://${COMMON_LICENSE_DIR}/AGPL-3.0-or-later;md5=a4af3f9f0c0fc9de318e4df46665906e \
file://${COMMON_LICENSE_DIR}/FSFAP;md5=232368338ef6dc99de71c2e05ff12176 \
file://${COMMON_LICENSE_DIR}/MPL-1.1;md5=1d38e87ed8d522c49f04e1efe0fab3ab \
file://${COMMON_LICENSE_DIR}/MPL-2.0;md5=815ca599c9df247a0c7f619bab123dad \
file://${COMMON_LICENSE_DIR}/CC-BY-3.0;md5=dfa02b5755629022e267f10b9c0a2ab7 \
file://${COMMON_LICENSE_DIR}/CC-BY-4.0;md5=9b33bbd06fb58995fb0e299cd38d1838 \
file://${COMMON_LICENSE_DIR}/CC-BY-SA-4.0;md5=4084714af41157e38872e798eb3fe1b1 \
file://${COMMON_LICENSE_DIR}/CC0-1.0;md5=0ceb3372c9595f0a8067e55da801e4a1 \
file://${COMMON_LICENSE_DIR}/NCSA;md5=1b5fdec70ee13ad8a91667f16c1959d7 \
file://${COMMON_LICENSE_DIR}/APSL-2.0;md5=f9e4701d9a216a87ba145bbe25f54c58 \
file://${COMMON_LICENSE_DIR}/IJG;md5=d9fc5ebaa95c14466091d25e0d34e688 \
file://${COMMON_LICENSE_DIR}/psutils;md5=29046009c1f269661e7b74196fb8f6a0 \
file://${COMMON_LICENSE_DIR}/Sendmail;md5=8037c42e05a5d4bfce06a44729fb6f1a \
file://${COMMON_LICENSE_DIR}/blessing;md5=d5407b61870d6dc19d0bdc04ae4cc654 \
file://${COMMON_LICENSE_DIR}/NTP;md5=0926fd147301b2a65e45e21adb3a6f14 \
file://${COMMON_LICENSE_DIR}/BSL-1.0;md5=65a7df9ad57aacf825fd252c4c33288c \
file://${COMMON_LICENSE_DIR}/GFDL-1.1-or-later;md5=03322744a1a73f36ebf29f98cced39a4 \
file://${COMMON_LICENSE_DIR}/GFDL-1.2-or-later;md5=9f58808219e9a42ff1228309d6f83dc6 \
file://${COMMON_LICENSE_DIR}/GFDL-1.3;md5=1083add59b39991c748ea70a92166959 \
file://${COMMON_LICENSE_DIR}/GFDL-1.3-or-later;md5=e0771ae6a62dc8a2e50b1d450fea66b7 \
file://${COMMON_LICENSE_DIR}/GFDL-1.3-no-invariants-or-later;md5=e0771ae6a62dc8a2e50b1d450fea66b7 \
file://${COMMON_LICENSE_DIR}/NPL-1.1;md5=f9c017c062c1b02462efb915d9f2cb63 \
file://${COMMON_LICENSE_DIR}/libtiff;md5=b99383975855adc28712577c9cd56485 \
file://${COMMON_LICENSE_DIR}/Vim;md5=676d28582e2dca824e7e309a9865eeb1 \
file://${COMMON_LICENSE_DIR}/curl;md5=f7adb1397db248527ffed14d947e445c \
file://${COMMON_LICENSE_DIR}/EUPL-1.1;md5=3f12b8134016fd7ba5a010afd690abaa \
file://${COMMON_LICENSE_DIR}/OFL-1.1;md5=fac3a519e5e9eb96316656e0ca4f2b90 \
file://${COMMON_LICENSE_DIR}/OFL-1.1-RFN;md5=2680fce30f17e5fed9bcebd9336e5b78 \
file://${COMMON_LICENSE_DIR}/FTL;md5=f0bf6b09ee8b02121ed10709d9e49d8b \
file://${COMMON_LICENSE_DIR}/Info-ZIP;md5=83a1c8ea099b3b58beb6e55dcbe4c15f \
file://${COMMON_LICENSE_DIR}/Interbase-1.0;md5=f65304bc0e87e6700fe1e4ab5affdc6f \
file://${COMMON_LICENSE_DIR}/Unicode-DFS-2016;md5=907371994d651afe53e98adc27824669 \
file://${COMMON_LICENSE_DIR}/SISSL;md5=fded06bff75eb4a2899bd051e2e128f5 \
file://${COMMON_LICENSE_DIR}/LPPL-1.3a;md5=8e2e8e1428b39cd78927c2ad28734ff7 \
file://${COMMON_LICENSE_DIR}/SGI-B-2.0;md5=5f5dd7bd973dff1594131b1e9c7981f1 \
file://${COMMON_LICENSE_DIR}/PSF-2.0;md5=76c1502273262a5ebefb50dfb20d7c4f \
file://${COMMON_LICENSE_DIR}/X11;md5=87f08485cf6ba3c63a00eda8ecba7f1d \
file://${COMMON_LICENSE_DIR}/OLDAP-2.8;md5=bb28ada4fbb5c3f52c233899b2e410a5 \
file://${COMMON_LICENSE_DIR}/PostgreSQL;md5=a9c78964f52e27f4c01140a1a16da8e2 \
file://${COMMON_LICENSE_DIR}/OPUBL-1.0;md5=99367d4750dbf0ae6cc74209ddd52f6d \
"

ARM_SYSTEMREADY_LINUX_DISTRO_INSTALL_SIZE = "6144"

TEST_SUITES = "${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "arm_systemready_fedora_unattended", "", d)}"

ISO_LABEL = "${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "Fedora-S-dvd-aarch64-39", "", d)}"
BOOT_CATALOG = "${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "boot.catalog", "", d)}"
BOOT_IMAGE = "${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "EFI/BOOT/BOOTAA64.EFI", "", d)}"
EFI_IMAGE = "${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "images/efiboot.img", "", d)}"

PV = "39.1.5"
SRC_URI = "\
    https://download.fedoraproject.org/pub/fedora/linux/releases/39/Server/aarch64/iso/Fedora-Server-dvd-aarch64-39-1.5.iso;unpack=0;downloadfilename=${ISO_IMAGE_NAME}.iso;name=fedora_iso_image \
    file://unattended-boot-conf/Fedora/ks.cfg\
    "
SRC_URI[fedora_iso_image.sha256sum] = "d19dc2a39758155fa53e6fd555d0d173ccc8175b55dea48002d499f39cb30ce0"

modifyiso() {
    UNATTENDED_CONF_DIR="${UNPACKDIR}/unattended-boot-conf/Fedora"
    
    cp "${UNATTENDED_CONF_DIR}/ks.cfg" ${EXTRACTED_ISO_TEMP_DIR}
    sed -i 's/set default="1"/set default="0"/g' "${EXTRACTED_ISO_TEMP_DIR}/EFI/BOOT/grub.cfg"
    sed -i 's/set timeout=60/set timeout=0/g' "${EXTRACTED_ISO_TEMP_DIR}/EFI/BOOT/grub.cfg"
    sed -i '0,/vmlinuz/s/vmlinuz/& inst.ks=hd:LABEL=Fedora-S-dvd-aarch64-39:\/ks.cfg/' "${EXTRACTED_ISO_TEMP_DIR}/EFI/BOOT/grub.cfg"
}
