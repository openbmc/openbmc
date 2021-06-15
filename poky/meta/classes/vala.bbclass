# Everyone needs vala-native and targets need vala, too,
# because that is where target builds look for .vapi files.
#
VALADEPENDS = ""
VALADEPENDS_class-target = "vala"
DEPENDS_append = " vala-native ${VALADEPENDS}"

# Our patched version of Vala looks in STAGING_DATADIR for .vapi files
export STAGING_DATADIR
# Upstream Vala >= 0.11 looks in XDG_DATA_DIRS for .vapi files
export XDG_DATA_DIRS = "${STAGING_DATADIR}:${STAGING_LIBDIR}"

# Package additional files
FILES_${PN}-dev += "\
    ${datadir}/vala/vapi/*.vapi \
    ${datadir}/vala/vapi/*.deps \
    ${datadir}/gir-1.0 \
"

# Remove vapigen.m4 that is bundled with tarballs
# because it does not yet have our cross-compile fixes
do_configure_prepend() {
        rm -f ${S}/m4/vapigen.m4
}
