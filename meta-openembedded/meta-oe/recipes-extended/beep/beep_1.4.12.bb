SUMMARY = "beep allows you to have the PC speaker issue beeps and beep patterns"
DESCRIPTION = "beep allows you to have the PC speaker issue beeps and beep \
patterns with given frequencies, durations, and spacing."
HOMEPAGE = "https://github.com/spkr-beep/beep"
BUGTRACKER = "https://github.com/spkr-beep/beep/issues"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/spkr-beep/beep.git;protocol=https;branch=master \
           file://0001-beep-library-Make-it-compatible-with-c99.patch"
SRCREV = "11453a79f2cea81832329b06ca3a284aa7a0a52e"
S = "${WORKDIR}/git"

EXTRA_OEMAKE = "prefix='${prefix}' CFLAGS='${CFLAGS}' LDFLAGS='${LDFLAGS}'"

do_install() {
    oe_runmake install DESTDIR='${D}'
}
