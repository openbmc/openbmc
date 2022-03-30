SUMMARY = "beep allows you to have the PC speaker issue beeps and beep patterns"
DESCRIPTION = "beep allows you to have the PC speaker issue beeps and beep \
patterns with given frequencies, durations, and spacing."
HOMEPAGE = "https://github.com/spkr-beep/beep"
BUGTRACKER = "https://github.com/spkr-beep/beep/issues"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/spkr-beep/beep.git;protocol=https;branch=master \
           file://0001-Do-not-use-Werror-as-it-fails-with-newer-clang-11.patch \
"
SRCREV = "8b85ddd09f73b9fd7caa5679298781a57af194ac"
S = "${WORKDIR}/git"

EXTRA_OEMAKE = " \
    COMPILER_gcc='${CC}' \
    LINKER_gcc='${CC}' \
    COMPILER_clang=no \
    LINKER_clang=no \
"

EXTRA_OEMAKE:toolchain-clang = " \
    COMPILER_clang='${CC}' \
    LINKER_clang='${CC}' \
    COMPILER_gcc=no \
    LINKER_gcc=no \
"

do_install() {
    oe_runmake install DESTDIR='${D}'
}
