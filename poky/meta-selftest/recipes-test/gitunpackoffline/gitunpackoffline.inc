SUMMARY = "Test recipe for fetching git submodules"
HOMEPAGE = "https://git.yoctoproject.org/git/matchbox-panel-2"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

INHIBIT_DEFAULT_DEPS = "1"

TAGVALUE = "2.10"

# Deliberately have a tag which has to be resolved but ensure do_unpack doesn't access the network again.
SRC_URI = "git://git.yoctoproject.org/git/matchbox-panel-2;branch=master;protocol=https"
SRC_URI:append:gitunpack-enable-recipe = ";tag=${TAGVALUE}"
SRCREV = "f82ca3f42510fb3ef10f598b393eb373a2c34ca7"
SRCREV:gitunpack-enable-recipe = ""

S = "${WORKDIR}/git"
