DESCRIPTION = "autossh is a program to start a copy of ssh and monitor it, restarting it as necessary should it die or stop passing traffic"
HOMEPAGE = "https://www.harding.motd.ca/autossh/"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://autossh.spec;md5=80a6701134723fd3420e733b46a0eb97"

SRC_URI = "https://www.harding.motd.ca/autossh/${BP}.tgz"
SRC_URI[md5sum] = "2b804bc1bf6d2f2afaa526d02df7c0a2"
SRC_URI[sha256sum] = "5fc3cee3361ca1615af862364c480593171d0c54ec156de79fc421e31ae21277"

RDEPENDS_${PN} = "ssh"

CFLAGS_prepend = "-I${WORKDIR}/build "

inherit autotools

EXTRA_OECONF="--with-ssh=/usr/bin/ssh"

do_compile_append() {
        cp ${WORKDIR}/autossh-${PV}/CHANGES ${WORKDIR}/build
        cp ${WORKDIR}/autossh-${PV}/README ${WORKDIR}/build
        cp ${WORKDIR}/autossh-${PV}/autossh.host ${WORKDIR}/build
        cp ${WORKDIR}/autossh-${PV}/rscreen ${WORKDIR}/build
        cp ${WORKDIR}/autossh-${PV}/autossh.1 ${WORKDIR}/build
        cp ${WORKDIR}/autossh-${PV}/autossh.spec ${WORKDIR}/build
}

do_install_append() {
        rm -rf ${D}${datadir}/examples
}
