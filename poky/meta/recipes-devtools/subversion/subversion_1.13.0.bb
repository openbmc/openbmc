SUMMARY = "Subversion (svn) version control system client"
HOMEPAGE = "http://subversion.apache.org"
DESCRIPTION = "Subversion is an open source version control system."
SECTION = "console/network"
LICENSE = "Apache-2 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6487ae7094d359fa90fb9c4096e52e2b"

DEPENDS = "apr-util serf sqlite3 file lz4"
DEPENDS_append_class-native = " file-replacement-native"

SRC_URI = "${APACHE_MIRROR}/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://disable_macos.patch \
           file://0001-Fix-libtool-name-in-configure.ac.patch \
           file://serfmacro.patch \
           "

SRC_URI[md5sum] = "3004b4dae18bf45a0b6ea4ef8820064d"
SRC_URI[sha256sum] = "bc50ce2c3faa7b1ae9103c432017df98dfd989c4239f9f8270bb3a314ed9e5bd"

inherit autotools pkgconfig gettext

CVE_PRODUCT = "apache:subversion"

PACKAGECONFIG ?= ""

PACKAGECONFIG[boost] = "--with-boost=${RECIPE_SYSROOT}${exec_prefix},--without-boost,boost"
PACKAGECONFIG[sasl] = "--with-sasl,--without-sasl,cyrus-sasl"
PACKAGECONFIG[gnome-keyring] = "--with-gnome-keyring,--without-gnome-keyring,glib-2.0 gnome-keyring"

EXTRA_OECONF = " \
    --with-apr=${STAGING_BINDIR_CROSS} \
    --with-apr-util=${STAGING_BINDIR_CROSS} \
    --without-apxs \
    --without-berkeley-db \
    --without-swig \
    --disable-keychain \
    --with-utf8proc=internal \
    ac_cv_path_RUBY=none \
"

EXTRA_OEMAKE += "pkgconfig_dir=${libdir}/pkgconfig"

acpaths = "-I build/ -I build/ac-macros/"

CPPFLAGS += "-P"
BUILD_CPPFLAGS += "-P"

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

RDEPENDS_${PN} = "serf"

BBCLASSEXTEND = "native nativesdk"
