SUMMARY = "LiSt Open Files tool"
DESCRIPTION = "Lsof is a Unix-specific diagnostic tool. \
Its name stands for LiSt Open Files, and it does just that."
HOMEPAGE = "http://people.freebsd.org/~abe/"
SECTION = "devel"
LICENSE = "Spencer-94"
LIC_FILES_CHKSUM = "file://COPYING;md5=a48ac97a8550eff12395a2c0d6151510"

SRC_URI = "git://github.com/lsof-org/lsof;branch=master;protocol=https \
           file://remove-host-information.patch"
SRCREV = "bbf320ce586a848f880bca7b758d50ae4c712624"

S = "${WORKDIR}/git"

inherit update-alternatives autotools pkgconfig manpages
PACKAGECONFIG[manpages] = ""

DEPENDS += "groff-native"

ALTERNATIVE:${PN} = "lsof"
ALTERNATIVE_LINK_NAME[lsof] = "${bindir}/lsof"
# Make our priority higher than busybox
ALTERNATIVE_PRIORITY = "100"
