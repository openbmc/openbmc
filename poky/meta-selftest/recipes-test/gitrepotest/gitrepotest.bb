SUMMARY = "Test recipe for git repo initialization"
HOMEPAGE = "https://git.yoctoproject.org/git/matchbox-panel-2"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

INHIBIT_DEFAULT_DEPS = "1"

PATCHTOOL="git"

SRC_URI = "git://git.yoctoproject.org/git/matchbox-panel-2;branch=master;protocol=https \
           file://0001-testpatch.patch \
          "

SRCREV = "f82ca3f42510fb3ef10f598b393eb373a2c34ca7"

S = "${WORKDIR}/git"
