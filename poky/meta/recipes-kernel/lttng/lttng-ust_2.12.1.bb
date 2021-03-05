SUMMARY = "Linux Trace Toolkit Userspace Tracer 2.x"
DESCRIPTION = "The LTTng UST 2.x package contains the userspace tracer library to trace userspace codes."
HOMEPAGE = "http://lttng.org/ust"
BUGTRACKER = "https://bugs.lttng.org/projects/lttng-ust"

LICENSE = "LGPLv2.1+ & MIT & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=c963eb366b781252b0bf0fdf1624d9e9 \
                    file://snprintf/snprintf.c;endline=32;md5=d3d544959d8a3782b2e07451be0a903c \
                    file://snprintf/various.h;endline=31;md5=89f2509b6b4682c4fc95255eec4abe44"

PYTHON_OPTION = "am_cv_python_pyexecdir='${PYTHON_SITEPACKAGES_DIR}' \
                 am_cv_python_pythondir='${PYTHON_SITEPACKAGES_DIR}' \
                 PYTHON_INCLUDE='-I${STAGING_INCDIR}/python${PYTHON_BASEVERSION}${PYTHON_ABI}' \
"

inherit autotools lib_package manpages python3native

include lttng-platforms.inc

EXTRA_OECONF = "--disable-numa"

DEPENDS = "liburcu util-linux"
RDEPENDS_${PN}-bin = "python3-core"

# For backwards compatibility after rename
RPROVIDES_${PN} = "lttng2-ust"
RREPLACES_${PN} = "lttng2-ust"
RCONFLICTS_${PN} = "lttng2-ust"

PE = "2"

SRC_URI = "https://lttng.org/files/lttng-ust/lttng-ust-${PV}.tar.bz2 \
           file://0001-python-lttngust-Makefile.am-Add-install-lib-to-setup.patch \
           "

SRC_URI[md5sum] = "11787d1df69b04dd7431614ab43b2e12"
SRC_URI[sha256sum] = "48a3948b168195123a749d22818809bd25127bb5f1a66458c3c012b210d2a051"

CVE_PRODUCT = "ust"

PACKAGECONFIG[examples] = "--enable-examples, --disable-examples,"
PACKAGECONFIG[manpages] = "--enable-man-pages, --disable-man-pages, asciidoc-native xmlto-native libxslt-native"
PACKAGECONFIG[python3-agent] = "--enable-python-agent ${PYTHON_OPTION}, --disable-python-agent, python3, python3"

FILES_${PN} += " ${PYTHON_SITEPACKAGES_DIR}/*"
FILES_${PN}-staticdev += " ${PYTHON_SITEPACKAGES_DIR}/*.a"
FILES_${PN}-dev += " ${PYTHON_SITEPACKAGES_DIR}/*.la"

do_install_append() {
        # Patch python tools to use Python 3; they should be source compatible, but
        # still refer to Python 2 in the shebang
        sed -i -e '1s,#!.*python.*,#!${bindir}/python3,' ${D}${bindir}/lttng-gen-tp
}
