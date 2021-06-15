SUMMARY = "A fast lightweight Text-to-speech engine"
DESCRIPTION = "Mimic is a fast, lightweight Text-to-speech engine developed by Mycroft A.I. and VocaliD, based on Carnegie Mellon University’s Flite (Festival-Lite) software. Mimic takes in text and reads it out loud to create a high quality voice."
HOMEPAGE = "https://mimic.mycroft.ai/"
SECTION = "multimedia"

# "Mimic is available under permissive BSD-like licenses"
LICENSE = "MIT-X & \
           PD & \
           CMU-Tex & \
           BSD & \
           BSD-2-Clause & \
           BSD-3-Clause & \
           flite & \
           (flite & Sun) & \
           BellBird & \
           Apache-2.0 \
           "
LIC_FILES_CHKSUM = "file://COPYING;md5=a2c2c7371b58b9cdeae0dc68846fe9f1"

DEPENDS = "curl-native libpcre2"

SRCREV = "adf655da0399530ac1b586590257847eb61be232"
SRC_URI = "git://github.com/MycroftAI/mimic1.git \
           file://0001-Fix-musl-compatibility.patch \
           file://0001-cmu_indic_lang-Make-cst_rx_not_indic-as-extern-decla.patch \
          "

inherit autotools

S = "${WORKDIR}/git"

CPPFLAGS_append = " -Wno-error"
