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
LIC_FILES_CHKSUM = "file://COPYING;md5=416ef1ca5167707fe381d7be33664a33"

DEPENDS = "curl-native icu"

SRCREV = "67e43bf0fa56008276b878ec3790aa5f32eb2a16"
SRC_URI = "git://github.com/MycroftAI/mimic.git"

inherit autotools

S = "${WORKDIR}/git"

CPPFLAGS_append = " -Wno-error"
