SUMMARY = "Tools of dm-thin device-mapper"
DESCRIPTION = "A suite of tools for manipulating the metadata of the dm-thin device-mapper target."
HOMEPAGE = "https://github.com/jthornber/thin-provisioning-tools"
LICENSE = "GPLv3"
SECTION = "devel"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
S = "${WORKDIR}/git"

SRC_URI = "git://github.com/jthornber/thin-provisioning-tools \
           file://0001-do-not-strip-pdata_tools-at-do_install.patch \
"

SRCREV = "49bfc12e9c7956c1ac134b24afbe1a6a602ce7d5"

DEPENDS += "expat libaio boost"

inherit autotools-brokensep
