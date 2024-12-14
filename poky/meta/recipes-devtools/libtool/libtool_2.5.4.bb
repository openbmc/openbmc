require libtool-${PV}.inc
require remove-buildpaths.inc

RDEPENDS:${PN} += "bash"

#
# We want the results of libtool-cross preserved - don't stage anything ourselves.
#
SYSROOT_DIRS_IGNORE += " \
    ${bindir} \
    ${datadir}/aclocal \
    ${datadir}/libtool/build-aux \
"

ACLOCALEXTRAPATH:class-target = ""

inherit multilib_script

MULTILIB_SCRIPTS = "${PN}:${bindir}/libtool"
