require arm-systemready-linux-distros.inc

LICENSE = "GPL-1.0-only & GPL-1.0-or-later & GPL-2.0-only & GPL-2.0-or-later \
           & GPL-3.0-only & GPL-3.0-or-later & LGPL-2.0-only \
           & LGPL-2.0-or-later & LGPL-2.1-only & LGPL-2.1-or-later \
           & LGPL-3.0-only & LGPL-3.0-or-later & BSD-3-Clause & BSD-4-Clause \
           & Artistic-1.0-Perl & Apache-1.0 & Apache-1.1 & Apache-2.0 & Zlib \
           & Python-2.0 & Ruby & PHP-3.01 & W3C-20150513 & OpenSSL & Sleepycat"
LIC_FILES_CHKSUM = "\
file://${COMMON_LICENSE_DIR}/GPL-1.0-only;md5=e9e36a9de734199567a4d769498f743d \
file://${COMMON_LICENSE_DIR}/GPL-1.0-or-later;md5=30c0b8a5048cc2f4be5ff15ef0d8cf61 \
file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6 \
file://${COMMON_LICENSE_DIR}/GPL-2.0-or-later;md5=fed54355545ffd980b814dab4a3b312c \
file://${COMMON_LICENSE_DIR}/GPL-3.0-only;md5=c79ff39f19dfec6d293b95dea7b07891 \
file://${COMMON_LICENSE_DIR}/GPL-3.0-or-later;md5=1c76c4cc354acaac30ed4d5eefea7245 \
file://${COMMON_LICENSE_DIR}/LGPL-2.0-only;md5=9427b8ccf5cf3df47c29110424c9641a \
file://${COMMON_LICENSE_DIR}/LGPL-2.0-or-later;md5=6d2d9952d88b50a51a5c73dc431d06c7 \
file://${COMMON_LICENSE_DIR}/LGPL-2.1-only;md5=1a6d268fd218675ffea8be556788b780 \
file://${COMMON_LICENSE_DIR}/LGPL-2.1-or-later;md5=2a4f4fd2128ea2f65047ee63fbca9f68 \
file://${COMMON_LICENSE_DIR}/LGPL-3.0-only;md5=bfccfe952269fff2b407dd11f2f3083b \
file://${COMMON_LICENSE_DIR}/LGPL-3.0-or-later;md5=c51d3eef3be114124d11349ca0d7e117 \
file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9 \
file://${COMMON_LICENSE_DIR}/BSD-4-Clause;md5=624d9e67e8ac41a78f6b6c2c55a83a2b \
file://${COMMON_LICENSE_DIR}/Artistic-1.0-Perl;md5=8feedd169dbd5738981843bd7d931f9f \
file://${COMMON_LICENSE_DIR}/Apache-1.0;md5=9f7a9503b805de9158a2a31a2cef4b70 \
file://${COMMON_LICENSE_DIR}/Apache-1.1;md5=61cc638ff95ff4f38f243855bcec4317 \
file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10 \
file://${COMMON_LICENSE_DIR}/Zlib;md5=87f239f408daca8a157858e192597633 \
file://${COMMON_LICENSE_DIR}/Python-2.0;md5=a5c8025e305fb49e6d405769358851f6 \
file://${COMMON_LICENSE_DIR}/Ruby;md5=105fc57d3f4d3122db32912f3e6107d0 \
file://${COMMON_LICENSE_DIR}/PHP-3.01;md5=3363e286b5882ec667a6ebd86e0d9d91 \
file://${COMMON_LICENSE_DIR}/W3C-20150513;md5=9ff23a699fca546a380855dd40d12d4f \
file://${COMMON_LICENSE_DIR}/OpenSSL;md5=4eb1764f3e65fafa1a25057f9082f2ae \
file://${COMMON_LICENSE_DIR}/Sleepycat;md5=1cbb64231c94198653282f3ccab88ffb \
"

PV = "11.7.0"
# netinst, DVD-1
ISO_TYPE = "netinst"
SRC_URI = "https://cdimage.debian.org/mirror/cdimage/archive/${PV}/arm64/iso-cd/debian-${PV}-arm64-${ISO_TYPE}.iso;unpack=0;downloadfilename=${ISO_IMAGE_NAME}.iso"
SRC_URI[sha256sum] = "174caba674fe3172938439257156b9cb8940bb5fd5ddf124256e81ec00ec460d"
