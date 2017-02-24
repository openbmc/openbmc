SUMMARY = "Stub implementation of gnome-doc-utils"
DESCRIPTION = "This recipe provides m4 macros from gnome-doc-utils project, so \
that dependent recipes can build. Gnome-doc-utils itself is no longer packaged because \
it requires libxml2 and python 2 at the same time, which can no longer be satisfied. "
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6 \
                    file://${COREBASE}/meta/files/common-licenses/LGPL-2.1;md5=1a6d268fd218675ffea8be556788b780"
SRC_URI = "file://gnome-doc-utils.m4"

PROVIDES = "gnome-doc-utils"

do_install_append() {
        install -d ${D}${datadir}/aclocal/
        install ${WORKDIR}/gnome-doc-utils.m4 ${D}${datadir}/aclocal/
}

FILES_${PN} += "${datadir}"

