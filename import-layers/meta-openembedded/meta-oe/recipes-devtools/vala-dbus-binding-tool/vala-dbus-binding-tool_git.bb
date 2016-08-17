require vala-dbus-binding-tool.inc

# Temporary remove negative preference, because there are 2 versions of 0.4.0 archives:
# the original one depending on libgee1.0 from
# http://downloads.freesmartphone.org/sources/${BPN}/${BP}.tar.bz2
# and new one depending on libgee0.8 from
# http://sourceforge.net/projects/freesmartphone/files/sources/
# The original one can be already deployed on various premirrors,
# so prefer the git recipe which doesn't suffer from this issue
# DEFAULT_PREFERENCE = "-1"

SRCREV = "742c04c2bb0743891904522ce47e50f9e5c99b12"
PV = "0.4.0+gitr${SRCPV}"

SRC_URI = "git://github.com/freesmartphone/vala-dbus-binding-tool.git;protocol=https \
    file://0001-configure.ac-don-t-use-dash-in-m4-macro-names.patch \
"
S = "${WORKDIR}/git"
