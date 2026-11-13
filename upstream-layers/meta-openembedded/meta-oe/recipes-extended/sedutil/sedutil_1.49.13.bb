DESCRIPTION = "A utility to manage self encrypting drives that conform \
to the Trusted Computing Group OPAL 2.0 SSC specification."
SUMMARY = "The Drive Trust Alliance Self Encrypting Drive Utility"
HOMEPAGE = "https://github.com/Drive-Trust-Alliance/sedutil"
SECTION = "console/utils"
LICENSE = "GPL-3.0-only"

LIC_FILES_CHKSUM = "file://Common/LICENSE.txt;md5=d32239bcb673463ab874e80d47fae504"

SRCREV = "1b0633afbe02bf5aa084f1efbedeeb065b00d209"
SRC_URI = "git://github.com/Drive-Trust-Alliance/sedutil.git;branch=develop;protocol=https;tag=${PV} \
           file://0001-Fix-build-on-big-endian-architectures.patch \
           file://0001-build-do-not-override-Automake-s-own-.INTERMEDIATE-t.patch \
           "

UPSTREAM_CHECK_GITTAGREGEX = "^(?P<pver>\d+(\.\d+)+)$"

DEPENDS = "systemd libnvme"
REQUIRED_DISTRO_FEATURES = "systemd"

inherit autotools-brokensep features_check

EXTRA_OEMAKE += "WERROR="

do_compile:prepend() {
    mkdir -p ${B}/linux
    echo '#define GIT_VERSION "${PV}"' > ${B}/linux/Version.h
}
