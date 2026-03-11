require arm-systemready-linux-distros.inc

# The Debian project does not provide a license manifest for the distributed ISO images.
# The following list only contains the SPDX license identifiers found on the deb
# packages from the ISO image and is not exhaustive.
# For more information about Debian licenses, including the non-free ones, refer to
# https://www.debian.org/legal/licenses/.
LICENSE = "AFL-2.0 & AFL-2.1 \
           & GPL-1.0-only & GPL-1.0-or-later & GPL-2.0-only & GPL-2.0-or-later & GPL-2.0-with-autoconf-exception \
           & GPL-2.0-with-OpenSSL-exception & GPL-3.0-only & GPL-3.0-or-later & GPL-3.0-with-autoconf-exception \
           & GPL-3-with-bison-exception & SMAIL_GPL & LGPL-2.0-only & LGPL-2.0-or-later & LGPL-2.1-only \
           & LGPL-2.1-or-later & LGPL-3.0-only & LGPL-3.0-or-later & BSD-2-Clause & BSD-3-Clause \
           & BSD-3-Clause-Clear & BSD-4-Clause & BSD-4-Clause-UC & TCP-wrappers & OLDAP-2.8 & PSF-2.0 & BSL-1.0 \
           & bzip2-1.0.6 & CC0-1.0 & Libpng & Latex2e & Unicode-TOU & Unicode-DFS-2016 & CC-BY-3.0 & CC-BY-SA-3.0 \
           & CC-BY-SA-4.0 & curl & MS-PL & NTP & FSFAP & FSFUL & FSFULLR & FSF-Unlimited & EDL-1.0 & Vim & FTL \
           & TCL & MPL-1.1 & MPL-2.0 & GFDL-1.1-or-later & GFDL-1.2-or-later & GFDL-1.3-no-invariants-or-later \
           & GFDL-1.3-no-invariants-only & Artistic-1.0 & Artistic-2.0 & Artistic-1.0-Perl & Apache-2.0 \
           & Apache-2.0-with-LLVM-exception & Zlib & Python-2.0 & OpenSSL & Sleepycat & Spencer-86 & MIT & MIT-CMU \
           & MIT-advertising & Beerware & Intel & X11 & ISC & IPL-1.0 & SSH-OpenSSH & SSH-short & RSA-MD & OPL-1.0 & PD"

LIC_FILES_CHKSUM = "\
file://${COMMON_LICENSE_DIR}/AFL-2.0;md5=f01c02e5eac69cff6b8c2cc474b8d468 \
file://${COMMON_LICENSE_DIR}/AFL-2.1;md5=e40039b90e182a056bcd9ad3e47ddd71 \
file://${COMMON_LICENSE_DIR}/GPL-1.0-only;md5=e9e36a9de734199567a4d769498f743d \
file://${COMMON_LICENSE_DIR}/GPL-1.0-or-later;md5=30c0b8a5048cc2f4be5ff15ef0d8cf61 \
file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6 \
file://${COMMON_LICENSE_DIR}/GPL-2.0-or-later;md5=fed54355545ffd980b814dab4a3b312c \
file://${COMMON_LICENSE_DIR}/GPL-2.0-with-autoconf-exception;md5=966c02a95037a9c7ad75a7597aea9c5f \
file://${COMMON_LICENSE_DIR}/GPL-2.0-with-OpenSSL-exception;md5=d9e4412f125e3e6f14efba8ce7b4604f \
file://${COMMON_LICENSE_DIR}/GPL-3.0-only;md5=c79ff39f19dfec6d293b95dea7b07891 \
file://${COMMON_LICENSE_DIR}/GPL-3.0-or-later;md5=1c76c4cc354acaac30ed4d5eefea7245 \
file://${COMMON_LICENSE_DIR}/GPL-3.0-with-autoconf-exception;md5=da26b415cb0faf9bfe6829f0ffa409ec \
file://${COMMON_LICENSE_DIR}/GPL-3-with-bison-exception;md5=6e1bac3dc21fcc4fa049cf5c407eb7a2 \
file://${COMMON_LICENSE_DIR}/SMAIL_GPL;md5=b948675029f79c64840e78881e91e1d4 \
file://${COMMON_LICENSE_DIR}/LGPL-2.0-only;md5=9427b8ccf5cf3df47c29110424c9641a \
file://${COMMON_LICENSE_DIR}/LGPL-2.0-or-later;md5=6d2d9952d88b50a51a5c73dc431d06c7 \
file://${COMMON_LICENSE_DIR}/LGPL-2.1-only;md5=1a6d268fd218675ffea8be556788b780 \
file://${COMMON_LICENSE_DIR}/LGPL-2.1-or-later;md5=2a4f4fd2128ea2f65047ee63fbca9f68 \
file://${COMMON_LICENSE_DIR}/LGPL-3.0-only;md5=bfccfe952269fff2b407dd11f2f3083b \
file://${COMMON_LICENSE_DIR}/LGPL-3.0-or-later;md5=c51d3eef3be114124d11349ca0d7e117 \
file://${COMMON_LICENSE_DIR}/BSD-2-Clause;md5=cb641bc04cda31daea161b1bc15da69f \
file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9 \
file://${COMMON_LICENSE_DIR}/BSD-3-Clause-Clear;md5=7a434440b651f4a472ca93716d01033a \
file://${COMMON_LICENSE_DIR}/BSD-4-Clause;md5=624d9e67e8ac41a78f6b6c2c55a83a2b \
file://${COMMON_LICENSE_DIR}/BSD-4-Clause-UC;md5=1da3cf8ad50cd8d5d1de3cfc53196d01 \
file://${COMMON_LICENSE_DIR}/TCP-wrappers;md5=83b1f59c3c52689f5652193e0cd5b1cf \
file://${COMMON_LICENSE_DIR}/OLDAP-2.8;md5=bb28ada4fbb5c3f52c233899b2e410a5 \
file://${COMMON_LICENSE_DIR}/PSF-2.0;md5=76c1502273262a5ebefb50dfb20d7c4f \
file://${COMMON_LICENSE_DIR}/BSL-1.0;md5=65a7df9ad57aacf825fd252c4c33288c \
file://${COMMON_LICENSE_DIR}/bzip2-1.0.6;md5=841c5495611ae95b13e80fa4a0627333 \
file://${COMMON_LICENSE_DIR}/CC0-1.0;md5=0ceb3372c9595f0a8067e55da801e4a1 \
file://${COMMON_LICENSE_DIR}/Libpng;md5=12b4ec50384c800bc568f519671b120c \
file://${COMMON_LICENSE_DIR}/Latex2e;md5=ef91d258f6a8d4d7f4db4d30adf38598 \
file://${COMMON_LICENSE_DIR}/Unicode-TOU;md5=666362dc5dba74f477af0f44fb85bd22 \
file://${COMMON_LICENSE_DIR}/Unicode-DFS-2016;md5=907371994d651afe53e98adc27824669 \
file://${COMMON_LICENSE_DIR}/CC-BY-3.0;md5=dfa02b5755629022e267f10b9c0a2ab7 \
file://${COMMON_LICENSE_DIR}/CC-BY-SA-3.0;md5=3248afbd148270ac7337a6f3e2558be5 \
file://${COMMON_LICENSE_DIR}/CC-BY-SA-4.0;md5=4084714af41157e38872e798eb3fe1b1 \
file://${COMMON_LICENSE_DIR}/curl;md5=f7adb1397db248527ffed14d947e445c \
file://${COMMON_LICENSE_DIR}/MS-PL;md5=b9cbca4f1a399b0c17b3521736e67848 \
file://${COMMON_LICENSE_DIR}/NTP;md5=0926fd147301b2a65e45e21adb3a6f14 \
file://${COMMON_LICENSE_DIR}/FSFAP;md5=232368338ef6dc99de71c2e05ff12176 \
file://${COMMON_LICENSE_DIR}/FSFUL;md5=dc74327e8d4dca295527a090d2af4ba4 \
file://${COMMON_LICENSE_DIR}/FSFULLR;md5=f0aa4b452548cc5d53a7772a9a90b3c0 \
file://${COMMON_LICENSE_DIR}/FSF-Unlimited;md5=06fadd9ae6adbcd5d8d545dac90b15f6 \
file://${COMMON_LICENSE_DIR}/EDL-1.0;md5=e06be17b8577bf6e2277a5c3c71b2d05 \
file://${COMMON_LICENSE_DIR}/Vim;md5=676d28582e2dca824e7e309a9865eeb1 \
file://${COMMON_LICENSE_DIR}/FTL;md5=f0bf6b09ee8b02121ed10709d9e49d8b \
file://${COMMON_LICENSE_DIR}/TCL;md5=5f7b23ac10d8f7cde16737bc896bb6fb \
file://${COMMON_LICENSE_DIR}/MPL-1.1;md5=1d38e87ed8d522c49f04e1efe0fab3ab \
file://${COMMON_LICENSE_DIR}/MPL-2.0;md5=815ca599c9df247a0c7f619bab123dad \
file://${COMMON_LICENSE_DIR}/GFDL-1.1-or-later;md5=03322744a1a73f36ebf29f98cced39a4 \
file://${COMMON_LICENSE_DIR}/GFDL-1.2-or-later;md5=9f58808219e9a42ff1228309d6f83dc6 \
file://${COMMON_LICENSE_DIR}/GFDL-1.3-no-invariants-or-later;md5=e0771ae6a62dc8a2e50b1d450fea66b7 \
file://${COMMON_LICENSE_DIR}/GFDL-1.3-no-invariants-only;md5=e0771ae6a62dc8a2e50b1d450fea66b7 \
file://${COMMON_LICENSE_DIR}/Artistic-1.0;md5=cda03bbdc3c1951996392b872397b798 \
file://${COMMON_LICENSE_DIR}/Artistic-2.0;md5=8bbc66f0ba93cec26ef526117e280266 \
file://${COMMON_LICENSE_DIR}/Artistic-1.0-Perl;md5=8feedd169dbd5738981843bd7d931f9f \
file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10 \
file://${COMMON_LICENSE_DIR}/Apache-2.0-with-LLVM-exception;md5=0bcd48c3bdfef0c9d9fd17726e4b7dab \
file://${COMMON_LICENSE_DIR}/Zlib;md5=87f239f408daca8a157858e192597633 \
file://${COMMON_LICENSE_DIR}/Python-2.0;md5=a5c8025e305fb49e6d405769358851f6 \
file://${COMMON_LICENSE_DIR}/OpenSSL;md5=4eb1764f3e65fafa1a25057f9082f2ae \
file://${COMMON_LICENSE_DIR}/Sleepycat;md5=1cbb64231c94198653282f3ccab88ffb \
file://${COMMON_LICENSE_DIR}/Spencer-86;md5=97ba797de74f88a17676473fab224843 \
file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
file://${COMMON_LICENSE_DIR}/MIT-CMU;md5=91b70218e0db8e063ed88cd532cb801d \
file://${COMMON_LICENSE_DIR}/MIT-advertising;md5=0f358dd6677661d482934070c7eeaeec \
file://${COMMON_LICENSE_DIR}/Beerware;md5=8db32780d0d8bbbdce0fa415e514cb89 \
file://${COMMON_LICENSE_DIR}/Intel;md5=ced5efc26449ecac834b4b71625a3410 \
file://${COMMON_LICENSE_DIR}/X11;md5=87f08485cf6ba3c63a00eda8ecba7f1d \
file://${COMMON_LICENSE_DIR}/ISC;md5=f3b90e78ea0cffb20bf5cca7947a896d \
file://${COMMON_LICENSE_DIR}/IPL-1.0;md5=be739b8845e6e98f99e206221fe9293b \
file://${COMMON_LICENSE_DIR}/SSH-OpenSSH;md5=3af632aae8cf01feb6ce2ed44bb7ed2e \
file://${COMMON_LICENSE_DIR}/SSH-short;md5=b73783010a430cadaabdc8ec0c0748f8 \
file://${COMMON_LICENSE_DIR}/RSA-MD;md5=9342e66a3fb8ddeebe449a85366f4acc \
file://${COMMON_LICENSE_DIR}/OPL-1.0;md5=acdf1e4398bd93dc137e271c50316324 \
file://${COMMON_LICENSE_DIR}/PD;md5=b3597d12946881e13cb3b548d1173851 \
"

PV = "12.8.0"
SRC_URI = "\
    https://cdimage.debian.org/mirror/cdimage/archive/12.8.0/arm64/iso-dvd/debian-12.8.0-arm64-DVD-1.iso;unpack=0;downloadfilename=${ISO_IMAGE_NAME}.iso;name=debian_iso_image \
    file://unattended-boot-conf/Debian/preseed.cfg \
    "
SRC_URI[debian_iso_image.sha256sum] = "8891fe48bb5a58ae54176eaa6440059bf852044d6b9ae77219e78f9ef8d65149"

TEST_SUITES = "${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "arm_systemready_debian_unattended", "", d)}"

ISO_LABEL = "${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "debian-12.8.0-arm64-1", "", d)}"
BOOT_CATALOG = "${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "boot.catalog", "", d)}"
BOOT_IMAGE = "${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "EFI/boot/bootaa64.efi", "", d)}"
EFI_IMAGE = "${@oe.utils.vartrue("DISTRO_UNATTENDED_INST_TESTS", "boot/grub/efi.img", "", d)}"

modifyiso() {
    UNATTENDED_CONF_DIR="${UNPACKDIR}/unattended-boot-conf/Debian"

    # Append the preseed.cfg file to the initrd
    gunzip ${EXTRACTED_ISO_TEMP_DIR}/install.a64/initrd.gz
    (cd ${UNATTENDED_CONF_DIR} && echo preseed.cfg | cpio -H newc -o -A -F ${EXTRACTED_ISO_TEMP_DIR}/install.a64/initrd)
    gzip ${EXTRACTED_ISO_TEMP_DIR}/install.a64/initrd

    #GRUB
    # Disable timeout
    sed -i '/^insmod gzio/ a set timeout=0' ${EXTRACTED_ISO_TEMP_DIR}/boot/grub/grub.cfg

    # Update default menu entry to select automated install
    sed -i '/^set timeout/ a set default="2>5"' ${EXTRACTED_ISO_TEMP_DIR}/boot/grub/grub.cfg

    # Update kernel boot parameters to enable more text based console output
    sed -i 's|linux    /install.a64/vmlinuz  auto=true priority=critical --- quiet|linux    /install.a64/vmlinuz auto=true priority=critical DEBIAN_FRONTEND=text --- nomodeset|g' ${EXTRACTED_ISO_TEMP_DIR}/boot/grub/grub.cfg
}
