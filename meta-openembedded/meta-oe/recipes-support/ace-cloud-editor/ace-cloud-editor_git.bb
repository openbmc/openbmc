DESCRIPTION = "Ace is a code editor written in JavaScript."
SUMMARY = "Ace is a code editor written in JavaScript. This repository has only generated files"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=794d11c5219c59c9efa2487c2b4066b2"

SRC_URI = "git://github.com/ajaxorg/ace-builds.git;protocol=https;branch=master"

PV = "02.07.17+git${SRCPV}"
SRCREV = "812e2c56aed246931a667f16c28b096e34597016"

FILES_${PN} = "${datadir}/ace-builds"

S = "${WORKDIR}/git"

DEPENDS += "rsync-native"

do_install () {
    install -d ${D}/${datadir}/ace-builds/src-noconflict
    rsync -r --exclude=".*" ${S}/src-noconflict/* ${D}/${datadir}/ace-builds/src-noconflict
}

