SUMMARY = "Enable NumLock in X11 sessions"
HOMEPAGE = "http://home.kde.org/~seli/numlockx/"
SECTION = "x11/apps"
LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dcb1cc75e21540a4a66b54e38d95b047"
DEPENDS = "virtual/libx11 libxtst"

SRC_URI = "http://pkgs.fedoraproject.org/repo/pkgs/numlockx/numlockx-${PV}.tar.gz/be9109370447eae23f6f3f8527bb1a67/numlockx-${PV}.tar.gz"

SRC_URI[md5sum] = "be9109370447eae23f6f3f8527bb1a67"
SRC_URI[sha256sum] = "e468eb9121c94c9089dc6a287eeb347e900ce04a14be37da29d7696cbce772e4"

inherit autotools distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECONF = "--x-includes=${STAGING_INCDIR} \
                --x-libraries=${STAGING_LIBDIR}"

do_configure_prepend() {
    # remove this from acinclude.m4 or build fails
    sed -i '/_AC_PATH_X_XMKMF/d' ${S}/acinclude.m4
}
