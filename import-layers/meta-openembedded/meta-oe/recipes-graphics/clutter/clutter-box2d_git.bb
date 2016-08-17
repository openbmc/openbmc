require clutter-box2d.inc

LIC_FILES_CHKSUM = "file://box2d/License.txt;md5=e5d39ad91f7dc4692dcdb1d85139ec6b"

# 0.12.1+gitAUTOINC+de5452e56b-r1/git/clutter-box2d/clutter-box2d.h:226:47:
#  fatal error: clutter-box2d/clutter-box2d-child.h: No such file or directory
# |  #include <clutter-box2d/clutter-box2d-child.h>
PNBLACKLIST[clutter-box2d] ?= "BROKEN: doesn't build with B!=S"

SRCREV = "de5452e56b537a11fd7f9453d048ff4b4793b5a2"
PV = "0.12.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://git.gnome.org/clutter-box2d.git"

S = "${WORKDIR}/git"

DEPENDS += "clutter-1.0"
PROVIDES = "clutter-box2d-1.6"
