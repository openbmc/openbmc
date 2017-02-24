SUMMARY = "A filesystem snapshot utility based on rsync"
HOMEPAGE = "http://www.rsnapshot.org"
BUGTRACKER = "https://sourceforge.net/projects/rsnapshot/"
SECTION = "console/network"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=892f569a555ba9c07a568a7c0c4fa63a"

RDEPENDS_${PN} = "rsync \
                  perl \
                  perl-module-dirhandle \
                  perl-module-cwd \
                  perl-module-getopt-std \
                  perl-module-file-path \
                  perl-module-file-stat \
                  perl-module-posix \
                  perl-module-fcntl \
                  perl-module-io-file \
                  perl-module-constant \
                  perl-module-overloading \
                 "

SRCREV = "27209563f924a22f510698ea225f53ea52f07cb4"
PV = "1.4.2+git${SRCPV}"

SRC_URI = "git://github.com/DrHyde/${BPN};branch=master;protocol=git \
           file://configure-fix-cmd_rsync.patch \
          "

S = "${WORKDIR}/git"

inherit autotools

# Fix rsnapshot.conf.default:
# don't inject the host path into target configs.
EXTRA_OECONF += "--without-cp \
                 --without-rm \
                 --without-du \
                 --without-ssh \
                 --without-logger \
                 --without-rsync \
                 ac_cv_path_PERL=${bindir}/perl \
                 ac_cv_path_MOUNT=${base_bindir}/mount \
                 ac_cv_path_UMOUNT=${base_bindir}/umount \
                "

# Create 't/include.ac' before starting the autoreconf to fix configure
# error: configure.ac:302: file 't/include.ac' does not exist
do_configure_prepend(){
	saved_dir=`pwd`
	cd ${S}; ./autogen.sh
	cd ${saved_dir}
}
