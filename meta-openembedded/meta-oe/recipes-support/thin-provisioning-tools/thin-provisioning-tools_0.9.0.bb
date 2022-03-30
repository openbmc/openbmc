SUMMARY = "Tools of dm-thin device-mapper"
DESCRIPTION = "A suite of tools for manipulating the metadata of the dm-thin device-mapper target."
HOMEPAGE = "https://github.com/jthornber/thin-provisioning-tools"
LICENSE = "GPL-3.0-only"
SECTION = "devel"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
S = "${WORKDIR}/git"

SRC_URI = "git://github.com/jthornber/thin-provisioning-tools;branch=main;protocol=https \
           file://0001-do-not-strip-pdata_tools-at-do_install.patch \
           file://use-sh-on-path.patch \
"

SRCREV = "d6d93c3157631b242a13a81d30f75453e576c55a"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

DEPENDS += "expat libaio boost"

inherit autotools-brokensep
