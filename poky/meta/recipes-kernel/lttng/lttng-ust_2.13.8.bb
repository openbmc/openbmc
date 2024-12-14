SUMMARY = "Linux Trace Toolkit Userspace Tracer 2.x"
DESCRIPTION = "The LTTng UST 2.x package contains the userspace tracer library to trace userspace codes."
HOMEPAGE = "http://lttng.org/ust"
BUGTRACKER = "https://bugs.lttng.org/projects/lttng-ust"

LICENSE = "LGPL-2.1-or-later & MIT & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a46577a38ad0c36ff6ff43ccf40c480f"

PYTHON_OPTION = "am_cv_python_pyexecdir='${PYTHON_SITEPACKAGES_DIR}' \
                 am_cv_python_pythondir='${PYTHON_SITEPACKAGES_DIR}' \
                 PYTHON_INCLUDE='-I${STAGING_INCDIR}/python${PYTHON_BASEVERSION}${PYTHON_ABI}' \
"

inherit autotools lib_package manpages python3native pkgconfig

include lttng-platforms.inc

EXTRA_OECONF = "--disable-numa"
CPPFLAGS:append:arm = "${@oe.utils.vartrue('DEBUG_BUILD', '-DUATOMIC_NO_LINK_ERROR', '', d)}"

DEPENDS = "liburcu util-linux"
RDEPENDS:${PN}-bin = "python3-core"

# For backwards compatibility after rename
RPROVIDES:${PN} = "lttng2-ust"
RREPLACES:${PN} = "lttng2-ust"
RCONFLICTS:${PN} = "lttng2-ust"

PE = "2"

SRC_URI = "https://lttng.org/files/lttng-ust/lttng-ust-${PV}.tar.bz2 \
           file://0001-python-lttngust-Makefile.am-Add-install-lib-to-setup.patch \
           file://0001-lttng-ust-common-link-with-liburcu-explicitly.patch \
           file://0001-Fix-Build-examples-when-rpath-is-stripped-from-in-bu.patch \
           "

SRC_URI[sha256sum] = "d4ef98dab9a37ad4f524ccafdfd50af4f266039b528dd5afabce78e49024d937"

CVE_PRODUCT = "ust"

PACKAGECONFIG[examples] = "--enable-examples, --disable-examples,"
PACKAGECONFIG[manpages] = "--enable-man-pages, --disable-man-pages, asciidoc-native xmlto-native libxslt-native"
PACKAGECONFIG[python3-agent] = "--enable-python-agent ${PYTHON_OPTION}, --disable-python-agent, python3, python3"

FILES:${PN} += " ${PYTHON_SITEPACKAGES_DIR}/*"
FILES:${PN}-staticdev += " ${PYTHON_SITEPACKAGES_DIR}/*.a"
FILES:${PN}-dev += " ${PYTHON_SITEPACKAGES_DIR}/*.la"

do_install:append() {
        # Patch python tools to use Python 3; they should be source compatible, but
        # still refer to Python 2 in the shebang
        sed -i -e '1s,#!.*python.*,#!${bindir}/python3,' ${D}${bindir}/lttng-gen-tp
}
