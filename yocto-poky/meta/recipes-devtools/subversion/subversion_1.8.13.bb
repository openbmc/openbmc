SUMMARY = "Subversion (svn) version control system client"
SECTION = "console/network"
DEPENDS = "apr-util serf sqlite3 file"
RDEPENDS_${PN} = "serf"
LICENSE = "Apache-2"
HOMEPAGE = "http://subversion.tigris.org"

BBCLASSEXTEND = "native"

inherit gettext pythonnative

SRC_URI = "${APACHE_MIRROR}/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://libtool2.patch \
           file://disable_macos.patch \
           file://serf.m4-Regex-modified-to-allow-D-in-paths.patch \
"
SRC_URI[md5sum] = "4413417b529d7bdf82f74e50df02e88b"
SRC_URI[sha256sum] = "1099cc68840753b48aedb3a27ebd1e2afbcc84ddb871412e5d500e843d607579"

LIC_FILES_CHKSUM = "file://LICENSE;md5=1c2f0119e478700b5428e26386cff923"

PACKAGECONFIG[sasl] = "--with-sasl,--without-sasl,cyrus-sasl"
PACKAGECONFIG[gnome-keyring] = "--with-gnome-keyring,--without-gnome-keyring,glib-2.0 gnome-keyring"

EXTRA_OECONF = " \
                --without-berkeley-db --without-apxs \
                --without-swig --with-apr=${STAGING_BINDIR_CROSS} \
                --with-apr-util=${STAGING_BINDIR_CROSS} \
                --disable-keychain \
                ac_cv_path_RUBY=none"

inherit autotools

export LDFLAGS += " -L${STAGING_LIBDIR} "
CPPFLAGS += "-P"
BUILD_CPPFLAGS += "-P"

acpaths = "-I build/ -I build/ac-macros/"

do_configure_prepend () {
	rm -f ${S}/libtool
	rm -f ${S}/build/libtool.m4 ${S}/build/ltmain.sh ${S}/build/ltoptions.m4 ${S}/build/ltsugar.m4 ${S}/build/ltversion.m4 ${S}/build/lt~obsolete.m4
	rm -f ${S}/aclocal.m4
	sed -i -e 's:with_sasl="/usr/local":with_sasl="${STAGING_DIR}":' ${S}/build/ac-macros/sasl.m4
}

#| x86_64-linux-libtool: install: warning: `/home/pokybuild/yocto-autobuilder/yocto-worker/nightly-qa-logrotate/build/build/tmp/work/x86_64-linux/subversion-native/1.8.9-r0/build/subversion/libsvn_ra_local/libsvn_ra_local-1.la' has not been installed in `/home/pokybuild/yocto-autobuilder/yocto-worker/nightly-qa-logrotate/build/build/tmp/sysroots/x86_64-linux/usr/lib'| x86_64-linux-libtool: install: warning: `/home/pokybuild/yocto-autobuilder/yocto-worker/nightly-qa-logrotate/build/build/tmp/work/x86_64-linux/subversion-native/1.8.9-r0/build/subversion/libsvn_repos/libsvn_repos-1.la' has not been installed in `/home/pokybuild/yocto-autobuilder/yocto-worker/nightly-qa-logrotate/build/build/tmp/sysroots/x86_64-linux/usr/lib'| /usr/bin/ld: cannot find -lsvn_delta-1| collect2: ld returned 1 exit status| x86_64-linux-libtool: install: warning: `/home/pokybuild/yocto-autobuilder/yocto-worker/nightly-qa-logrotate/build/build/tmp/work/x86_64-linux/subversion-native/1.8.9-r0/build/subversion/libsvn_ra_svn/libsvn_ra_svn-1.la' has not been installed in `/home/pokybuild/yocto-autobuilder/yocto-worker/nightly-qa-logrotate/build/build/tmp/sysroots/x86_64-linux/usr/lib'| x86_64-linux-libtool: install: warning: `/home/pokybuild/yocto-autobuilder/yocto-worker/nightly-qa-logrotate/build/build/tmp/work/x86_64-linux/subversion-native/1.8.9-r0/build/subversion/libsvn_ra_serf/libsvn_ra_serf-1.la' has not been installed in `/home/pokybuild/yocto-autobuilder/yocto-worker/nightly-qa-logrotate/build/build/tmp/sysroots/x86_64-linux/usr/lib'
#| x86_64-linux-libtool: install: error: relink `libsvn_ra_serf-1.la' with the above command before installing it
#| x86_64-linux-libtool: install: warning: `../../subversion/libsvn_repos/libsvn_repos-1.la' has not been installed in `/home/pokybuild/yocto-autobuilder/yocto-worker/nightly-qa-logrotate/build/build/tmp/sysroots/x86_64-linux/usr/lib'
#| /home/pokybuild/yocto-autobuilder/yocto-worker/nightly-qa-logrotate/build/build/tmp/work/x86_64-linux/subversion-native/1.8.9-r0/subversion-1.8.9/build-outputs.mk:1090: recipe for target 'install-serf-lib' failed
#| make: *** [install-serf-lib] Error 1
PARALLEL_MAKEINST = ""
