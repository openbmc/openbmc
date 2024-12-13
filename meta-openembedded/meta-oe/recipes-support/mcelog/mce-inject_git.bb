SUMMARY = "Linux machine check injector tool"
DESCRIPTION = "mce-inject allows to inject machine check errors on the \
software level into a running Linux kernel. This is intended for \
validation of the kernel machine check handler."
SECTION = "System Environment/Base"

SRC_URI = "git://git.kernel.org/pub/scm/utils/cpu/mce/mce-inject.git;branch=master"

SRCREV = "7668d820cadce2da9d90b72aab14c3e637ca47d6"

UPSTREAM_CHECK_COMMITS = "1"

DEPENDS = "bison-native"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://README;beginline=30;md5=94c18755082a2da9c9cf97cba3ad47d6"

S = "${WORKDIR}/git"

COMPATIBLE_HOST = '(x86_64.*|i.86.*)-linux'

inherit autotools-brokensep

EXTRA_OEMAKE = "destdir=${D} CFLAGS='${CFLAGS}'"
