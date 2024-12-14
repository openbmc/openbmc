SUMMARY = "Subversion (svn) version control system client"
HOMEPAGE = "http://subversion.apache.org"
DESCRIPTION = "Subversion is an open source version control system."
SECTION = "console/network"
LICENSE = "Apache-2.0 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6487ae7094d359fa90fb9c4096e52e2b"

DEPENDS = "apr-util serf sqlite3 file lz4"
DEPENDS:append:class-native = " file-replacement-native"

SRC_URI = "${APACHE_MIRROR}/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://serfmacro.patch \
           "

SRC_URI[sha256sum] = "44ead116e72e480f10f123c914bb6f9f8c041711c041ed7abff1b8634a199e3c"

inherit autotools pkgconfig gettext python3native

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

do_configure:prepend () {
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

RDEPENDS:${PN} = "serf"

BBCLASSEXTEND = "native nativesdk"
