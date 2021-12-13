DESCRIPTION = "libConfuse is a configuration file parser library"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=42fa47330d4051cd219f7d99d023de3a"

SRCREV = "a42aebf13db33afd575da6e63f55163d371f776d"
SRC_URI = "git://github.com/libconfuse/libconfuse.git;branch=master;protocol=https"

inherit autotools-brokensep pkgconfig gettext

S = "${WORKDIR}/git"

do_configure:prepend(){
    (cd ${S} && ${S}/autogen.sh)
}
