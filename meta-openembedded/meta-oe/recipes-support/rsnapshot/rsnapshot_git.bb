SUMMARY = "A filesystem snapshot utility based on rsync"
HOMEPAGE = "http://www.rsnapshot.org"
BUGTRACKER = "https://sourceforge.net/projects/rsnapshot/"
SECTION = "console/network"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=892f569a555ba9c07a568a7c0c4fa63a"

RDEPENDS:${PN} = "rsync \
                  perl \
                  perl-module-dirhandle \
                  perl-module-cwd \
                  perl-module-getopt-std \
                  perl-module-file-path \
                  perl-module-file-stat \
                  perl-module-file-spec \
                  perl-module-posix \
                  perl-module-fcntl \
                  perl-module-io-file \
                  perl-module-constant \
                  perl-module-overloading \
                  perl-module-ipc-open3 \
                 "

SRCREV = "1b943dbc7695d62fac5c0f9549ec696a538be19c"
PV = "1.4.5"

SRC_URI = "git://github.com/DrHyde/${BPN};branch=master;protocol=https \
           file://configure-fix-cmd_rsync.patch \
          "

S = "${WORKDIR}/git"

inherit autotools perlnative

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
do_configure:prepend(){
	saved_dir=`pwd`
	cd ${S}; ./autogen.sh
	cd ${saved_dir}
}
