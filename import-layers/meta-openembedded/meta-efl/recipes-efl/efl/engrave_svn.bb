SUMMARY = "Engrave is an Edje Editing Library"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=edf2d968b9eb026bfa82cccbd0e6f9f5"
# also requires yacc and lex on host
DEPENDS = "evas ecore flex"
PV = "0.0.0+svnr${SRCPV}"
SRCREV = "${EFL_SRCREV}"

inherit efl autotools-brokensep
SRC_URI = "${E_SVN}/OLD;module=${SRCNAME};protocol=http;scmdata=keep"
S = "${WORKDIR}/${SRCNAME}"

# engrave.l:5:35: fatal error: libengrave_la-engrave.h: No such file or directory
# http://errors.yoctoproject.org/Errors/Details/56597/
PNBLACKLIST[engrave] ?= "BROKEN: fails to build with latest oe-core"
