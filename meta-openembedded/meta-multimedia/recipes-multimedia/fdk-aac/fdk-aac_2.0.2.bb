SUMMARY = "FDK-AAC audio codec"

DESCRIPTION = "The Fraunhofer FDK AAC Codec Library for Android \
(\"FDK AAC Codec\") is software that implements the MPEG \
Advanced Audio Coding (\"AAC\") encoding and decoding scheme \
for digital audio."

HOMEPAGE = "https://www.iis.fraunhofer.de/en/ff/amm/impl.html"

LICENSE = "Fraunhofer_FDK_AAC_Codec_Library_for_Android"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://NOTICE;md5=5985e1e12f4afa710d64ed7bfd291875"

SRC_URI = "git://github.com/mstorsjo/fdk-aac.git;protocol=https;branch=master"
SRCREV = "801f67f671929311e0c9952c5f92d6e147c7b003"

S = "${WORKDIR}/git"

SRC_URI[md5sum] = "fef453b5d6ee28ff302c600b8cded3e7"
SRC_URI[sha256sum] = "07c2a64b098eb48b2e9d729d5e778c08f7d22f28adc8da7c3f92c58da1cbbd8e"

inherit autotools
