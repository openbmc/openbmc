SUMMARY = "The RPM package management system"
DESCRIPTION = "The RPM Package Manager (RPM) is a powerful command line driven \
package management system capable of installing, uninstalling, \
verifying, querying, and updating software packages. Each software \
package consists of an archive of files along with information about \
the package like its version, a description, etc."

SUMMARY_${PN}-libs = "Libraries for manipulating RPM packages"
DESCRIPTION_${PN}-libs = "This package contains the RPM shared libraries."

SUMMARY_${PN}-dev = "Development files for manipulating RPM packages"
DESCRIPTION_${PN}-dev = "This package contains the RPM C library and header files. These \
development files will simplify the process of writing programs that \
manipulate RPM packages and databases. These files are intended to \
simplify the process of creating graphical package managers or any \
other tools that need an intimate knowledge of RPM packages in order \
to function."

SUMMARY_${PN}-common = "Common RPM paths, scripts, documentation and configuration"
DESCRIPTION_${PN}-common = "The rpm-common package contains paths, scripts, documentation \
and configuration common between RPM Package Manager."

SUMMARY_${PN}-build = "Scripts and executable programs used to build packages"
DESCRIPTION_${PN}-build = "The rpm-build packagec ontains the scripts and executable programs \
that are used to build packages using the RPM Package Manager."

SUMMARY_python-rpm = "Python bindings for apps which will manupulate RPM packages"
DESCRIPTION_python-rpm = "The rpm-python package contains a module that permits applications \
written in the Python programming language to use the interface \
supplied by the RPM Package Manager libraries."

SUMMARY_perl-module-rpm = "Perl bindings for apps which will manipulate RPM packages"
DESCRIPTION_perl-modules-rpm = "The perl-modules-rpm package contains a module that permits applications \
written in the Perl programming language to use the interface \
supplied by the RPM Package Manager libraries."

SUMMARY_perl-module-rpm-dev = "Development components for perl bindings"
DESCRIPTION_perl-modules-rpm-dev = "Development items such as man pages for use with the Perl \
language bindings."

HOMEPAGE = "http://rpm5.org/"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "libpcre attr acl popt ossp-uuid file byacc-native"
DEPENDS_append_class-native = " file-replacement-native"

S = "${WORKDIR}/rpm"

# Apply various fixups that are unique to the CVS environment
do_fixup_unpack () {
	ln -sf ../syck ${S}/syck || :
	ln -sf ../lua ${S}/lua || :
	ln ${S}/rpmqv.c ${S}/rpmqv.cc || :
}

addtask fixup_unpack after do_unpack before do_patch

# This recipe is really designed for development... to Try out the latest
# community work in progress.
DEFAULT_PREFERENCE = "-1"

# rpm2cpio is a shell script, which is part of the rpm src.rpm.  It is needed
# in order to extract the distribution SRPM into a format we can extract...
SRC_URI = "cvs://anonymous@rpm5.org/cvs;tag=rpm-5_4;module=rpm \
	   cvs://anonymous@rpm5.org/cvs;tag=rpm-5_4;module=syck \
	   cvs://anonymous@rpm5.org/cvs;tag=rpm-5_4;module=lua \
	   file://rpm-log-auto-rm.patch \
	   file://rpm-db-reduce.patch \
	   file://perfile_rpmdeps.sh \
	   file://rpm-autogen.patch \
	   file://rpm-libsql-fix.patch \
	   file://header-include-fix.patch \
	   file://rpm-platform.patch \
	   file://rpm-showrc.patch \
	   file://rpm-tools-mtree-LDFLAGS.patch \
	   file://rpm-fileclass.patch \
	   file://rpm-canonarch.patch \
	   file://rpm-no-loopmsg.patch \
	   file://rpm-scriptletexechelper.patch \
	   file://pythondeps.sh \
	   file://rpmdeps-oecore.patch \
	   file://rpm-resolvedep.patch \
	   file://rpm-no-perl-urpm.patch \
	   file://rpm-macros.patch \
	   file://rpm-lua.patch \
	   file://rpm-ossp-uuid.patch \
	   file://rpm-packageorigin.patch \
	   file://rpm-pkgconfigdeps.patch \
	   file://uclibc-support.patch \
	   file://rpmatch.patch \
	   file://fstack-protector-configure-check.patch \
	   file://dbconvert.patch \
	   file://rpm-uuid-include.patch \
	   file://makefile-am-exec-hook.patch \
	   file://rpm-db_buffer_small.patch \
	   file://rpm-py-init.patch \
	   file://python-rpm-rpmsense.patch \
	   file://rpm-reloc-macros.patch \
	   file://rpm-platform2.patch \
	   file://rpm-remove-sykcparse-decl.patch \
	   file://debugedit-segv.patch \
	   file://debugedit-valid-file-to-fix-segment-fault.patch \
	   file://rpm-platform-file-fix.patch \
	   file://rpm-lsb-compatibility.patch \
	   file://rpm-tag-generate-endian-conversion-fix.patch \
	   file://rpm-hardlink-segfault-fix.patch \
	   file://rpm-payload-use-hashed-inode.patch \
	   file://rpm-fix-logio-cp.patch \
	   file://rpm-db5-or-db6.patch \
	   file://rpm-rpmpgp-fix.patch \
	   file://rpm-disable-Wno-override-init.patch \
	   file://rpm-realpath.patch \
	  "

# Uncomment the following line to enable platform score debugging
# This is useful when identifying issues with Smart being unable
# to process certain package feeds.
#SRC_URI += "file://rpm-debug-platform.patch"

inherit autotools gettext

acpaths = "-I ${S}/db/dist/aclocal -I ${S}/db/dist/aclocal_java"

# Specify the default rpm macros in terms of adjustable variables
rpm_macros = "%{_usrlibrpm}/macros:%{_usrlibrpm}/poky/macros:%{_usrlibrpm}/poky/%{_target}/macros:%{_etcrpm}/macros.*:%{_etcrpm}/macros:%{_etcrpm}/%{_target}/macros:~/.oerpmmacros"
rpm_macros_class-native = "%{_usrlibrpm}/macros:%{_usrlibrpm}/poky/macros:%{_usrlibrpm}/poky/%{_target}/macros:~/.oerpmmacros"
rpm_macros_class-nativesdk = "%{_usrlibrpm}/macros:%{_usrlibrpm}/poky/macros:%{_usrlibrpm}/poky/%{_target}/macros:~/.oerpmmacros"

# sqlite lua tcl augeas nss gcrypt neon xz xar keyutils perl selinux

# Note: perl and sqlite w/o db specified does not currently work.
#       tcl, augeas, nss, gcrypt, xar and keyutils support is untested.
PACKAGECONFIG ??= "db bzip2 zlib beecrypt openssl libelf python"

PACKAGECONFIG[bzip2] = "--with-bzip2,--without-bzip2,bzip2,"
PACKAGECONFIG[xz] = "--with-xz,--without-xz,xz,"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib,"
PACKAGECONFIG[xar] = "--with-xar,--without-xar,xar,"

WITH_PYTHON = " --with-python=${PYTHON_BASEVERSION} \
		--with-python-inc-dir=${STAGING_INCDIR}/python${PYTHON_BASEVERSION} \
		--with-python-lib-dir=${libdir}/python${PYTHON_BASEVERSION}/site-packages \
		--without-pythonembed"
PACKAGECONFIG[python] = "${WITH_PYTHON},--without-python,python,"

# Perl modules are not built, but they could be enabled fairly easily
# the perl module creation and installation would need to be patched.
# (currently has host perl contamination issues)
WITH_PERL = "--with-perl --without-perlembed --without-perl-urpm"
WITHOUT_PERL = "--without-perl --without-perl-urpm"
PACKAGECONFIG[perl] = "${WITH_PERL},${WITHOUT_PERL},perl,"

# The --with-dbsql will only tell RPM to check for support, db
# may or may not be built w/ the dbsql support.
WITH_DB = "--with-db --with-dbsql --without-db-tools-integrated"
PACKAGECONFIG[db] = "${WITH_DB},--without-db,db,"

PACKAGECONFIG[sqlite] = "--with-sqlite,--without-sqlite,sqlite3,"

PACKAGECONFIG[beecrypt] = "--with-beecrypt=external,--without-beecrypt,beecrypt,"
PACKAGECONFIG[openssl] = "--with-openssl,--without-openssl,openssl,"
PACKAGECONFIG[nss] = "--with-nss,--without-nss,nss,"
PACKAGECONFIG[gcrypt] = "--with-gcrypt,--without-gcrypt,gcrypt,"
PACKAGECONFIG[keyutils] = "--with-keyutils,--without-keyutils,keyutils,"
PACKAGECONFIG[libelf] = "--with-libelf,--without-libelf,elfutils,"

WITH_SELINUX = "--with-selinux --with-sepol --with-semanage"
WITHOUT_SELINUX = "--without-selinux --without-sepol --without-semanage"
PACKAGECONFIG[selinux] = "${WITH_SELINUX},${WITHOUT_SELINUX},libselinux,"

WITH_NEON = "--with-neon --with-libproxy --with-expat --without-gssapi"
WITHOUT_NEON = "--without-neon --without-libproxy --without-expat --without-gssapi"
PACKAGECONFIG[neon] = "${WITH_NEON},${WITHOUT_NEON},neon expat,"

PACKAGECONFIG[lua] = "--with-lua,--without-lua,expat,"
PACKAGECONFIG[tcl] = "--with-tcl,--without-tcl,tcl,"

PACKAGECONFIG[augeas] = "--with-augeas,--without-augeas,augeas,"

EXTRA_OECONF += "--verbose \
		--sysconfdir=${sysconfdir} \
		--with-file \
		--with-path-magic=%{_usrlibrpm}/../../share/misc/magic.mgc \
		--with-syck=internal \
		--without-readline \
		--without-libtasn1 \
		--without-pakchois \
		--without-gnutls \
		--with-pcre \
		--enable-utf8 \
		--with-uuid \
		--with-attr \
		--with-acl \
		--with-popt=external \
		--with-pthreads \
		--without-cudf \
		--without-ficl \
		--without-aterm \
		--without-nix \
		--without-bash \
		--without-rc \
		--without-js \
		--without-gpsee \
		--without-ruby \
		--without-squirrel \
		--with-build-extlibdep \
		--with-build-maxextlibdep \
		--without-valgrind \
		--disable-openmp \
		--enable-build-pic \
		--enable-build-versionscript \
		--enable-build-warnings \
		--enable-build-debug \
		--enable-maintainer-mode \
		--with-path-macros=${rpm_macros} \
		--with-path-lib=${libdir}/rpm \
		--with-bugreport=http://bugzilla.yoctoproject.org \
		--program-prefix= \
		YACC=byacc"

CFLAGS_append = " -DRPM_VENDOR_WINDRIVER -DRPM_VENDOR_POKY -DRPM_VENDOR_OE"

LDFLAGS_append_libc-uclibc = "-lrt -lpthread"

PACKAGES = "${PN}-dbg ${PN} ${PN}-doc ${PN}-libs ${PN}-dev ${PN}-staticdev ${PN}-common ${PN}-build python-rpm-dbg python-rpm-staticdev python-rpm-dev python-rpm perl-module-rpm perl-module-rpm-dev ${PN}-locale"

SOLIBS = "5.4.so"

# Based on %files section in the rpm.spec

FILES_${PN} =  "${bindir}/rpm \
		${bindir}/rpmconstant \
		${libdir}/rpm/rpm.* \
		${libdir}/rpm/tgpg \
		${libdir}/rpm/macros \
		${libdir}/rpm/rpmpopt \
		${libdir}/rpm/rpm2cpio \
		${libdir}/rpm/vcheck \
		${libdir}/rpm/helpers \
		${libdir}/rpm/qf \
		${libdir}/rpm/cpuinfo.yaml \
		${libdir}/rpm/bin/mtree \
		${libdir}/rpm/bin/rpmkey \
		${libdir}/rpm/bin/rpmrepo \
		${libdir}/rpm/bin/rpmrepo.real \
		${libdir}/rpm/bin/rpmspecdump \
		${libdir}/rpm/bin/rpmspecdump.real \
		${libdir}/rpm/bin/wget \
		${localstatedir}/cache \
		${localstatedir}/cache/rpm \
		${localstatedir}/cache/wdj \
		${localstatedir}/lib \
		${localstatedir}/lib/rpm \
		${localstatedir}/lib/wdj \
		${bindir}/rpm.real \
		${bindir}/rpmconstant.real \
		${bindir}/rpm2cpio.real \
		"

FILES_${PN}-dbg += "${libdir}/rpm/.debug \
		${libdir}/rpm/bin/.debug \
		"

FILES_${PN}-common = "${bindir}/rpm2cpio \
		${bindir}/gendiff \
		${sysconfdir}/rpm \
		${localstatedir}/spool/repackage \
		"

FILES_${PN}-libs = "${libdir}/librpm-*.so \
		${libdir}/librpmconstant-*.so \
		${libdir}/librpmdb-*.so \
		${libdir}/librpmio-*.so \
		${libdir}/librpmmisc-*.so \
		${libdir}/librpmbuild-*.so \
		"

FILES_${PN}-build = "${prefix}/src/rpm \
		${bindir}/rpmbuild \
		${bindir}/rpmbuild.real \
		${libdir}/rpm/brp-* \
		${libdir}/rpm/check-files \
		${libdir}/rpm/cross-build \
		${libdir}/rpm/find-debuginfo.sh \
		${libdir}/rpm/find-lang.sh \
		${libdir}/rpm/find-prov.pl \
		${libdir}/rpm/find-provides.perl \
		${libdir}/rpm/find-req.pl \
		${libdir}/rpm/find-requires.perl \
		${libdir}/rpm/getpo.sh \
		${libdir}/rpm/http.req \
		${libdir}/rpm/javadeps.sh \
		${libdir}/rpm/mono-find-provides \
		${libdir}/rpm/mono-find-requires \
		${libdir}/rpm/executabledeps.sh \
		${libdir}/rpm/libtooldeps.sh \
		${libdir}/rpm/osgideps.pl \
		${libdir}/rpm/perldeps.pl \
		${libdir}/rpm/perl.prov \
		${libdir}/rpm/perl.req \
		${libdir}/rpm/php.prov \
		${libdir}/rpm/php.req \
		${libdir}/rpm/pkgconfigdeps.sh \
		${libdir}/rpm/pythondeps.sh \
		${libdir}/rpm/bin/debugedit \
		${libdir}/rpm/bin/debugedit.real \
		${libdir}/rpm/bin/rpmcache \
		${libdir}/rpm/bin/rpmcache.real \
		${libdir}/rpm/bin/rpmcmp \
		${libdir}/rpm/bin/rpmcmp.real \
		${libdir}/rpm/bin/rpmdeps \
		${libdir}/rpm/bin/rpmdeps.real \
		${libdir}/rpm/bin/rpmdeps-oecore \
		${libdir}/rpm/bin/rpmdeps-oecore.real \
		${libdir}/rpm/bin/rpmdigest \
		${libdir}/rpm/bin/rpmdigest.real \
		${libdir}/rpm/bin/abi-compliance-checker.pl \
		${libdir}/rpm/bin/api-sanity-autotest.pl \
		${libdir}/rpm/bin/chroot \
		${libdir}/rpm/bin/cp \
		${libdir}/rpm/bin/dbsql \
		${libdir}/rpm/bin/find \
		${libdir}/rpm/bin/install-sh \
		${libdir}/rpm/bin/lua \
		${libdir}/rpm/bin/luac \
		${libdir}/rpm/bin/mkinstalldirs \
		${libdir}/rpm/bin/rpmlua \
		${libdir}/rpm/bin/rpmluac \
		${libdir}/rpm/bin/sqlite3 \
		${libdir}/rpm/macros.d/cmake \
		${libdir}/rpm/macros.d/java \
		${libdir}/rpm/macros.d/libtool \
		${libdir}/rpm/macros.d/mandriva \
		${libdir}/rpm/macros.d/mono \
		${libdir}/rpm/macros.d/perl \
		${libdir}/rpm/macros.d/php \
		${libdir}/rpm/macros.d/pkgconfig \
		${libdir}/rpm/macros.d/python \
		${libdir}/rpm/macros.d/ruby \
		${libdir}/rpm/macros.d/selinux \
		${libdir}/rpm/macros.d/tcl \
		${libdir}/rpm/macros.rpmbuild \
		${libdir}/rpm/u_pkg.sh \
		${libdir}/rpm/vpkg-provides.sh \
		${libdir}/rpm/vpkg-provides2.sh \
		${libdir}/rpm/perfile_rpmdeps.sh \
		"
RDEPENDS_${PN} = "base-files run-postinsts"
RDEPENDS_${PN}_class-native = ""
RDEPENDS_${PN}_class-nativesdk = ""
RDEPENDS_${PN}-build = "file bash perl"

RDEPENDS_python-rpm = "${PN}"

FILES_python-rpm-dbg = "${libdir}/python*/site-packages/rpm/.debug/_*"
FILES_python-rpm-dev = "${libdir}/python*/site-packages/rpm/*.la"
FILES_python-rpm-staticdev = "${libdir}/python*/site-packages/rpm/*.a"
FILES_python-rpm = "${libdir}/python*/site-packages/rpm"

FILES_perl-module-rpm = "${libdir}/perl/*/* \
		"

FILES_perl-module-rpm-dev = "${prefix}/share/man/man3/RPM* \
		"

RDEPENDS_${PN}-dev += "bash"

FILES_${PN}-dev = "${includedir}/rpm \
		${libdir}/librpm.la \
		${libdir}/librpm.so \
		${libdir}/librpmconstant.la \
		${libdir}/librpmconstant.so \
		${libdir}/librpmdb.la \
		${libdir}/librpmdb.so \
		${libdir}/librpmio.la \
		${libdir}/librpmio.so \
		${libdir}/librpmmisc.la \
		${libdir}/librpmmisc.so \
		${libdir}/librpmbuild.la \
		${libdir}/librpmbuild.so \
		${libdir}/rpm/lib/liblua.la \
		${libdir}/pkgconfig/rpm.pc \
		${libdir}/rpm/rpmdb_loadcvt \
		"

FILES_${PN}-staticdev = " \
		${libdir}/librpm.a \
		${libdir}/librpmconstant.a \
		${libdir}/librpmdb.a \
		${libdir}/librpmio.a \
		${libdir}/librpmmisc.a \
		${libdir}/librpmbuild.a \
		${libdir}/rpm/lib/liblua.a \
		"

do_configure() {
	# Disable tests!
	echo "all:" > ${S}/tests/Makefile.am
	sed -e 's/@__MKDIR_P@/%{__mkdir} -p/' -i ${S}/macros/macros.in

	( cd ${S}; ${S}/autogen.sh )

	# NASTY hack to make sure configure files the right pkg-config file...
	sed -e 's/pkg-config --exists uuid/pkg-config --exists ossp-uuid/g' \
	    -e 's/pkg-config uuid/pkg-config ossp-uuid/g' -i ${S}/configure

	( cd ${S}/syck ; set +e ; rm -- -l* ; make distclean ) || :

	export varprefix=${localstatedir}
	oe_runconf
}

do_install_append() {
	sed -i -e 's,%__scriptlet_requires,#%%__scriptlet_requires,' ${D}/${libdir}/rpm/macros
	sed -i -e 's,%__perl_provides,#%%__perl_provides,' ${D}/${libdir}/rpm/macros ${D}/${libdir}/rpm/macros.d/*
	sed -i -e 's,%__perl_requires,#%%__perl_requires,' ${D}/${libdir}/rpm/macros ${D}/${libdir}/rpm/macros.d/*
	sed -i -e 's,%_repackage_all_erasures[^_].*,%_repackage_all_erasures 0,' ${D}/${libdir}/rpm/macros
	sed -i -e 's,^#%_openall_before_chroot.*,%_openall_before_chroot\t1,' ${D}/${libdir}/rpm/macros

	# Enable Debian style arbitrary tags...
	sed -i -e 's,%_arbitrary_tags[^_].*,%_arbitrary_tags %{_arbitrary_tags_debian},' ${D}/${libdir}/rpm/macros

	install -m 0755 ${WORKDIR}/pythondeps.sh ${D}/${libdir}/rpm/pythondeps.sh
	install -m 0755 ${WORKDIR}/perfile_rpmdeps.sh ${D}/${libdir}/rpm/perfile_rpmdeps.sh

	# Remove unpackaged files (based on list in rpm.spec)
	rm -f ${D}/${libdir}/rpm/{Specfile.pm,cpanflute,cpanflute2,rpmdiff,rpmdiff.cgi,sql.prov,sql.req,tcl.req,trpm}

	rm -f ${D}/${mandir}/man8/rpmcache.8*
	rm -f ${D}/${mandir}/man8/rpmgraph.8*
	rm -f ${D}/${mandir}/*/man8/rpmcache.8*
	rm -f ${D}/${mandir}/*/man8/rpmgraph.8*
	rm -rf ${D}/${mandir}/{fr,ko}

	rm -f ${D}/${includedir}/popt.h
	rm -f ${D}/${libdir}/libpopt.*
	rm -f ${D}/${libdir}/pkgconfig/popt.pc
	rm -f ${D}/${datadir}/locale/*/LC_MESSAGES/popt.mo
	rm -f ${D}/${mandir}/man3/popt.3

	rm -f ${D}/${mandir}/man1/xar.1*
	rm -f ${D}/${bindir}/xar
	rm -rf ${D}/${includedir}/xar
	rm -f ${D}/${libdir}/libxar*

	rm -f ${D}/${bindir}/lz*
	rm -f ${D}/${bindir}/unlzma
	rm -f ${D}/${bindir}/unxz
	rm -f ${D}/${bindir}/xz*
	rm -rf ${D}/${includedir}/lzma*
	rm -f ${D}/${mandir}/man1/lz*.1
	rm -f ${D}/${libdir}/pkgconfig/liblzma*

	rm -f ${D}/${libdir}/python%{with_python_version}/site-packages/*.{a,la}
	rm -f ${D}/${libdir}/python%{with_python_version}/site-packages/rpm/*.{a,la}

	#find ${D}/${libdir}/perl5 -type f -a \( -name perllocal.pod -o -name .packlist \
	#	-o \( -name '*.bs' -a -empty \) \) -exec rm -f {} ';'
	#find ${D}/${libdir}/perl5 -type d -depth -exec rmdir {} 2>/dev/null ';'

	# We don't want the default macro set
	rm -rf ${D}/${libdir}/rpm/{i[3456]86*,athlon*,pentium*,x86_64*,alpha*,sparc*,ia64*,ppc*,s390*,armv[34][lb]*,armv[345]*,mips*,noarch*}

	rm -f ${D}/${libdir}/rpm/dbconvert.sh

	rm -f ${D}/${libdir}/rpm/libsqldb.*

	# We don't want, nor need the Mandriva multiarch items
	rm -f ${D}/${bindir}/multiarch-dispatch
	rm -f ${D}/${bindir}/multiarch-platform
	rm -f ${D}/${libdir}/rpm/check-multiarch-files
	rm -f ${D}/${libdir}/rpm/mkmultiarch
	rm -f ${D}/${includedir}/multiarch-dispatch.h

	rm -f ${D}/${libdir}/rpm/gstreamer.sh
	rm -f ${D}/${libdir}/rpm/gem_helper.rb
	rm -f ${D}/${libdir}/rpm/rubygems.rb
	rm -f ${D}/${libdir}/rpm/kmod-deps.sh
	rm -f ${D}/${libdir}/rpm/pythoneggs.py
	rm -f ${D}/${libdir}/rpm/macros.d/kernel
	rm -f ${D}/${libdir}/rpm/macros.d/gstreamer
	rm -f ${D}/${libdir}/rpm/bin/mgo
	rm -f ${D}/${libdir}/rpm/bin/dbconvert
	rm -f ${D}/${libdir}/rpm/bin/pom2spec

	rm -rf ${D}/var/lib/wdj ${D}/var/cache/wdj
	rm -f ${D}/${libdir}/rpm/bin/api-sanity-checker.pl

}

add_native_wrapper() {
        create_wrapper ${D}/${bindir}/rpm \
		RPM_USRLIBRPM='`dirname $''realpath`'/${@os.path.relpath(d.getVar('libdir', True), d.getVar('bindir', True))}/rpm \
		RPM_ETCRPM='$'{RPM_ETCRPM-'`dirname $''realpath`'/${@os.path.relpath(d.getVar('sysconfdir', True), d.getVar('bindir', True))}/rpm} \
		RPM_LOCALEDIRRPM='`dirname $''realpath`'/${@os.path.relpath(d.getVar('datadir', True), d.getVar('bindir', True))}/locale

        create_wrapper ${D}/${bindir}/rpm2cpio \
		RPM_USRLIBRPM='`dirname $''realpath`'/${@os.path.relpath(d.getVar('libdir', True), d.getVar('bindir', True))}/rpm \
		RPM_ETCRPM='$'{RPM_ETCRPM-'`dirname $''realpath`'/${@os.path.relpath(d.getVar('sysconfdir', True), d.getVar('bindir', True))}/rpm} \
		RPM_LOCALEDIRRPM='`dirname $''realpath`'/${@os.path.relpath(d.getVar('datadir', True), d.getVar('bindir', True))}/locale

        create_wrapper ${D}/${bindir}/rpmbuild \
		RPM_USRLIBRPM='`dirname $''realpath`'/${@os.path.relpath(d.getVar('libdir', True), d.getVar('bindir', True))}/rpm \
		RPM_ETCRPM='$'{RPM_ETCRPM-'`dirname $''realpath`'/${@os.path.relpath(d.getVar('sysconfdir', True), d.getVar('bindir', True))}/rpm} \
		RPM_LOCALEDIRRPM='`dirname $''realpath`'/${@os.path.relpath(d.getVar('datadir', True), d.getVar('bindir', True))}/locale

        create_wrapper ${D}/${bindir}/rpmconstant \
		RPM_USRLIBRPM='`dirname $''realpath`'/${@os.path.relpath(d.getVar('libdir', True), d.getVar('bindir', True))}/rpm \
		RPM_ETCRPM='$'{RPM_ETCRPM-'`dirname $''realpath`'/${@os.path.relpath(d.getVar('sysconfdir', True), d.getVar('bindir', True))}/rpm} \
		RPM_LOCALEDIRRPM='`dirname $''realpath`'/${@os.path.relpath(d.getVar('datadir', True), d.getVar('bindir', True))}/locale

	for rpm_binary in ${D}/${libdir}/rpm/bin/rpm* ${D}/${libdir}/rpm/bin/debugedit; do
        	create_wrapper $rpm_binary \
			RPM_USRLIBRPM='`dirname $''realpath`'/${@os.path.relpath(d.getVar('libdir', True), d.getVar('bindir', True))}/rpm \
			RPM_ETCRPM='$'{RPM_ETCRPM-'`dirname $''realpath`'/${@os.path.relpath(d.getVar('sysconfdir', True), d.getVar('bindir', True))}/rpm} \
			RPM_LOCALEDIRRPM='`dirname $''realpath`'/${@os.path.relpath(d.getVar('datadir', True), d.getVar('bindir', True))}/locale
	done
}

do_install_append_class-native() {
	add_native_wrapper
}

do_install_append_class-nativesdk() {
	add_native_wrapper
}

BBCLASSEXTEND = "native nativesdk"
