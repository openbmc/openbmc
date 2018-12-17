SUMMARY = "Tools of dm-thin device-mapper"
DESCRIPTION = "A suite of tools for manipulating the metadata of the dm-thin device-mapper target."
HOMEPAGE = "https://github.com/jthornber/thin-provisioning-tools"
LICENSE = "GPLv3"
SECTION = "devel"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
S = "${WORKDIR}/git"

SRC_URI = "git://github.com/jthornber/thin-provisioning-tools \
           file://0001-do-not-strip-pdata_tools-at-do_install.patch \
           file://0001-fix-compile-failed-with-libc-musl.patch \
"

SRCREV = "6f936992b8e6208a7838fcf1ec87c5bd3a694a77"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

DEPENDS += "expat libaio boost"

inherit autotools-brokensep
