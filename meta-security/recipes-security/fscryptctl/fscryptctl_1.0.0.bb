SUMMARY = "low-level tool handling Linux filesystem encryption"
DESCIPTION = "fscryptctl is a low-level tool written in C that handles raw keys and manages \
policies for Linux filesystem encryption (https://lwn.net/Articles/639427). \
For a tool that presents a higher level interface and manages metadata, key \
generation, key wrapping, PAM integration, and passphrase hashing, see \
fscrypt (https://github.com/google/fscrypt)."
HOMEPAGE = "https://github.com/google/fscryptctl"
SECTION = "base"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "56b898c896240328adef7407090215abbe9ee03d"
SRC_URI = "git://github.com/google/fscryptctl.git"

S = "${WORKDIR}/git"

do_install() {
    oe_runmake DESTDIR=${D} PREFIX=/usr install
}

RRECOMMENDS_${PN} += "\
    keyutils \
    kernel-module-cbc \
    kernel-module-cts \
    kernel-module-ecb \
    kernel-module-xts \
"
