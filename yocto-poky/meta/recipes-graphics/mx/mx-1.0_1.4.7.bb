require mx.inc

# The 1.4.7 tag does not build against cogl 1.14, pull in a revision with a fix
SRCREV = "9b1db6b8060bd00b121a692f942404a24ae2960f"
PV = "1.4.7+git${SRCPV}"

SRC_URI = "git://github.com/clutter-project/mx.git;branch=mx-1.4 \
	   file://fix-build-dir.patch \
	   file://fix-test-includes.patch \
	  "
S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=fbc093901857fcd118f065f900982c24 \
                    file://mx/mx-widget.c;beginline=8;endline=20;md5=13bba3c973a72414a701e1e87b5ee879"
