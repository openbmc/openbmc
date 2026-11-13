#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

def gnome_verdir(v):
    # Upstream treats a major of 40+ as a flag day to switch between major.minor
    # versioned directories, or just major.
    # See get_majmin() in
    # https://gitlab.gnome.org/Infrastructure/openshift-images/gnome-release-service/-/blob/main/gnome_release_service/gnome_release_system/utils.py
    parts = v.split(".")
    major = int(parts[0])
    if major >= 40:
        return major
    else:
        return ".".join(parts[:2])


GNOME_COMPRESS_TYPE ?= "xz"
SECTION ?= "x11/gnome"
GNOMEBN ?= "${BPN}"
SRC_URI = "${GNOME_MIRROR}/${GNOMEBN}/${@gnome_verdir("${PV}")}/${GNOMEBN}-${PV}.tar.${GNOME_COMPRESS_TYPE};name=archive"

S = "${UNPACKDIR}/${GNOMEBN}-${PV}"

FILES:${PN} += "${datadir}/application-registry  \
                ${datadir}/mime-info \
                ${datadir}/mime/packages \
                ${datadir}/mime/application \
                ${datadir}/gnome-2.0 \
                ${datadir}/polkit* \
                ${datadir}/GConf \
                ${datadir}/glib-2.0/schemas \
                ${datadir}/appdata \
                ${datadir}/icons \
"

FILES:${PN}-doc += "${datadir}/devhelp"

GNOMEBASEBUILDCLASS ??= "meson"
inherit pkgconfig
inherit_defer ${GNOMEBASEBUILDCLASS}

do_install:append() {
	rm -rf ${D}${localstatedir}/lib/scrollkeeper/*
	rm -rf ${D}${localstatedir}/scrollkeeper/*
	rm -f ${D}${datadir}/applications/*.cache
}
