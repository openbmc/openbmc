SUMMARY = "Linux Trace Toolkit Userspace Tracer 2.x"
DESCRIPTION = "The LTTng UST 2.x package contains the userspace tracer library to trace userspace codes."
HOMEPAGE = "http://lttng.org/ust"
BUGTRACKER = "https://bugs.lttng.org/projects/lttng-ust"

LICENSE = "LGPLv2.1+ & MIT & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=c963eb366b781252b0bf0fdf1624d9e9 \
                    file://snprintf/snprintf.c;endline=32;md5=d3d544959d8a3782b2e07451be0a903c \
                    file://snprintf/various.h;endline=31;md5=89f2509b6b4682c4fc95255eec4abe44"

inherit autotools lib_package

DEPENDS = "liburcu util-linux"
RDEPENDS_${PN}-bin = "python3-core"

# For backwards compatibility after rename
RPROVIDES_${PN} = "lttng2-ust"
RREPLACES_${PN} = "lttng2-ust"
RCONFLICTS_${PN} = "lttng2-ust"

SRCREV = "514a87f3b64181e384399935a5708a8f85b0cc83"
PE = "2"
PV = "2.8.1+git${SRCPV}"

SRC_URI = "git://git.lttng.org/lttng-ust.git;branch=stable-2.8 \
           file://lttng-ust-doc-examples-disable.patch \
          "

EXTRA_OECONF += "--disable-man-pages"

do_install_append() {
        # Patch python tools to use Python 3; they should be source compatible, but
        # still refer to Python 2 in the shebang
        sed -i -e '1s,#!.*python.*,#!${bindir}/python3,' ${D}${bindir}/lttng-gen-tp
}


S = "${WORKDIR}/git"

do_configure_prepend () {
	( cd ${S}; ${S}/bootstrap )
}
