# Recipe created by recipetool
# This is the basis of a recipe and may need further editing in order to be fully functional.
# (Feel free to remove these comments when editing.)
#
# WARNING: the following LICENSE and LIC_FILES_CHKSUM values are best guesses - it is
# your responsibility to verify that the values are complete and correct.
#
# NOTE: multiple licenses have been detected; if that is correct you should separate
# these in the LICENSE value using & if the multiple licenses all apply, or | if there
# is a choice between the multiple licenses. If in doubt, check the accompanying
# documentation to determine which situation is applicable.
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=dc9db360e0bbd4e46672f3fd91dd6c4b \
                    file://packages/deb/copyright;md5=945e94e5bf43f31d4c0fa0a1c91e9055"

PACKAGE_ARCH = "all"
SRC_URI = "\
  git://github.com/google/glog.git \
  file://0001-Added-GOOGLE_GLOG_DLL_DECL-compiler-error-fix.patch \
  file://0002-Added-support-for-symbolize-to-extract-symbols-using.patch"

# export SBINDIR = "${STAGING_BINDIR}/../sbin"
# export LIBDIR = "${STAGING_LIBDIR}"
# export INCDIR = "${STAGING_INCDIR}"

DEPENDS += "glibc"
RDEPENDS_${PN} += "glibc"

# Modify these as desired
PV = "1.0+git${SRCPV}"
SRCREV = "v0.3.4"

S = "${WORKDIR}/git"

# NOTE: unable to map the following CMake package dependencies: gflags
# NOTE: the following library dependencies are unknown, ignoring: unwind
#       (this is based on recipes that have previously been built and packaged)
inherit pkgconfig autotools

# Specify any options you want to pass to cmake using EXTRA_OECMAKE:
# EXTRA_OEMAKE = "CPPFLAGS='-D GOOGLE_GLOG_DLL_DECL' CFLAGS='-D GOOGLE_GLOG_DLL_DECL'"

