SUMMARY = "The RPM package management system"
DESCRIPTION = "The RPM Package Manager (RPM) is a powerful command line driven \
package management system capable of installing, uninstalling, \
verifying, querying, and updating software packages. Each software \
package consists of an archive of files along with information about \
the package like its version, a description, etc."

SUMMARY:${PN}-dev = "Development files for manipulating RPM packages"
DESCRIPTION:${PN}-dev = "This package contains the RPM C library and header files. These \
development files will simplify the process of writing programs that \
manipulate RPM packages and databases. These files are intended to \
simplify the process of creating graphical package managers or any \
other tools that need an intimate knowledge of RPM packages in order \
to function."

SUMMARY:python3-rpm = "Python bindings for apps which will manupulate RPM packages"
DESCRIPTION:python3-rpm = "The python3-rpm package contains a module that permits applications \
written in the Python programming language to use the interface \
supplied by the RPM Package Manager libraries."

HOMEPAGE = "http://www.rpm.org"

# libraries are also LGPL - how to express this?
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=c4eec0c20c6034b9407a09945b48a43f"

SRC_URI = "git://github.com/rpm-software-management/rpm;branch=rpm-4.19.x;protocol=https \
           file://0001-Do-not-add-an-unsatisfiable-dependency-when-building.patch \
           file://0001-Do-not-read-config-files-from-HOME.patch \
           file://0001-When-cross-installing-execute-package-scriptlets-wit.patch \
           file://0001-Do-not-reset-the-PATH-environment-variable-before-ru.patch \
           file://0002-Add-support-for-prefixing-etc-from-RPM_ETCCONFIGDIR-.patch \
           file://0001-Do-not-hardcode-lib-rpm-as-the-installation-path-for.patch \
           file://0001-Add-a-color-setting-for-mips64_n32-binaries.patch \
           file://0001-perl-disable-auto-reqs.patch \
           file://0016-rpmscript.c-change-logging-level-around-scriptlets-t.patch \
           file://0001-lib-transaction.c-fix-file-conflicts-for-MIPS64-N32.patch \
           file://0001-build-pack.c-do-not-insert-payloadflags-into-.rpm-me.patch \
           file://0001-CMakeLists.txt-look-for-lua-with-pkg-config-rather-t.patch \
           file://0002-rpmio-rpmglob.c-avoid-using-GLOB_BRACE-if-undefined-.patch \
           "

PE = "1"
SRCREV = "13b4521341781293c41ac898aa9c2d2f6bc1f21d"

S = "${WORKDIR}/git"

DEPENDS = "lua libgcrypt file popt xz bzip2 elfutils python3 sqlite3 zstd"
DEPENDS:append:class-native = " file-replacement-native bzip2-replacement-native"

EXTRA_OECMAKE:append = " -D__CURL:FILEPATH=curl"
EXTRA_OECMAKE:append:libc-musl = " -DENABLE_NLS=OFF -DENABLE_OPENMP=OFF"

# --sysconfdir prevents rpm from attempting to access machine-specific configuration in sysroot/etc; we need to have it in rootfs
# --localstatedir prevents rpm from writing its database to native sysroot when building images
EXTRA_OECMAKE:append:class-native = " -DCMAKE_INSTALL_SYSCONFDIR:PATH=/etc -DCMAKE_INSTALL_LOCALSTATEDIR:PATH=/var"
EXTRA_OECMAKE:append:class-nativesdk = " -DCMAKE_INSTALL_SYSCONFDIR:PATH=/etc -DCMAKE_INSTALL_FULL_SYSCONFDIR=/etc"

inherit cmake gettext pkgconfig python3targetconfig
OECMAKE_GENERATOR = "Unix Makefiles"

BBCLASSEXTEND = "native nativesdk"

PACKAGECONFIG ??= "internal-openpgp"

PACKAGECONFIG[plugins] = "-DENABLE_PLUGINS=ON,-DENABLE_PLUGINS=OFF"
PACKAGECONFIG[testsuite] = "-DENABLE_TESTSUITE=ON,-DENABLE_TESTSUITE=OFF"

# Deprecated! https://fedoraproject.org/wiki/Changes/RpmSequoia
PACKAGECONFIG[internal-openpgp] = "-DWITH_INTERNAL_OPENPGP=ON,-DWITH_INTERNAL_OPENPGP=OFF"

PACKAGECONFIG[cap] = "-DWITH_CAP=ON,-DWITH_CAP=OFF"
PACKAGECONFIG[acl] = "-DWITH_ACL=ON,-DWITH_ACL=OFF"
PACKAGECONFIG[archive] = "-DWITH_ARCHIVE=ON,-DWITH_ARCHIVE=OFF,libarchive"
PACKAGECONFIG[selinux] = "-DWITH_SELINUX=ON,-DWITH_SELINUX=OFF,libselinux"
PACKAGECONFIG[dbus] = "-DWITH_DBUS=ON,-DWITH_DBUS=OFF"
PACKAGECONFIG[audit] = "-DWITH_AUDIT=ON,-DWITH_AUDIT=OFF,audit"
PACKAGECONFIG[fsverity] = "-DWITH_FSVERITY=ON,-DWITH_FSVERITY=OFF"
PACKAGECONFIG[imaevm] = "-DWITH_IMAEVM=ON,-DWITH_IMAEVM=OFF,ima-evm-utils"
PACKAGECONFIG[fapolicyd] = "-DWITH_FAPOLICYD=ON,-DWITH_FAPOLICYD=OFF"
PACKAGECONFIG[readline] = "-DWITH_READLINE=ON,-DWITH_READLINE=OFF,readline"

# Direct rpm-native to read configuration from our sysroot, not the one it was compiled in
# libmagic also has sysroot path contamination, so override it

WRAPPER_TOOLS = " \
   ${bindir}/rpm \
   ${bindir}/rpm2archive \
   ${bindir}/rpm2cpio \
   ${bindir}/rpmbuild \
   ${bindir}/rpmdb \
   ${bindir}/rpmgraph \
   ${bindir}/rpmkeys \
   ${bindir}/rpmsign \
   ${bindir}/rpmspec \
   ${libdir}/rpm/rpmdeps \
"

do_install:append:class-native() {
        for tool in ${WRAPPER_TOOLS}; do
                test -x ${D}$tool && create_wrapper ${D}$tool \
                        RPM_CONFIGDIR=${STAGING_LIBDIR_NATIVE}/rpm \
                        RPM_ETCCONFIGDIR=${STAGING_DIR_NATIVE} \
                        MAGIC=${STAGING_DIR_NATIVE}${datadir_native}/misc/magic.mgc \
                        RPM_NO_CHROOT_FOR_SCRIPTS=1
        done
}

do_install:append:class-nativesdk() {
        rm -rf ${D}/var

	mkdir -p ${D}${SDKPATHNATIVE}/environment-setup.d
	cat <<- EOF > ${D}${SDKPATHNATIVE}/environment-setup.d/rpm.sh
		export RPM_CONFIGDIR="${libdir}/rpm"
		export RPM_ETCCONFIGDIR="${SDKPATHNATIVE}"
		export RPM_NO_CHROOT_FOR_SCRIPTS=1
	EOF
}

# Rpm's make install creates var/tmp which clashes with base-files packaging
do_install:append:class-target() {
    rm -rf ${D}/var
}
do_install:append:class-nativesdk() {
    rm -rf ${D}${SDKPATHNATIVE}/var
    # Ensure find-debuginfo is located correctly inside SDK
    mkdir -p ${D}${libdir}/rpm
    echo "%__find_debuginfo   ${SDKPATHNATIVE}/usr/bin/find-debuginfo" >> ${D}${libdir}/rpm/macros
}

do_install:append () {
	sed -i -e 's:${HOSTTOOLS_DIR}/::g' \
            -e 's:${STAGING_DIR_NATIVE}/::g' \
	    ${D}/${libdir}/rpm/macros
	sed -i -e 's:${RECIPE_SYSROOT}/::g' \
	    ${D}/${libdir}/cmake/rpm/rpm-targets.cmake

}

FILES:${PN} += "${libdir}/rpm-plugins/*.so \
               "
FILES:${PN}:append:class-nativesdk = " ${SDKPATHNATIVE}/environment-setup.d/rpm.sh"

FILES:${PN}-dev += "${libdir}/rpm-plugins/*.la \
                    "
PACKAGE_BEFORE_PN += "${PN}-build ${PN}-sign ${PN}-archive"

RRECOMMENDS:${PN} += "rpm-sign rpm-archive"

FILES:${PN}-build = "\
    ${bindir}/rpmbuild \
    ${bindir}/gendiff \
    ${bindir}/rpmspec \
    ${libdir}/librpmbuild.so.* \
    ${libdir}/rpm/brp-* \
    ${libdir}/rpm/check-* \
    ${libdir}/rpm/sepdebugcrcfix \
    ${libdir}/rpm/find-lang.sh \
    ${libdir}/rpm/sysusers.sh \
    ${libdir}/rpm/*provides* \
    ${libdir}/rpm/*requires* \
    ${libdir}/rpm/*deps* \
    ${libdir}/rpm/*.prov \
    ${libdir}/rpm/*.req \
    ${libdir}/rpm/config.* \
    ${libdir}/rpm/mkinstalldirs \
    ${libdir}/rpm/macros.p* \
    ${libdir}/rpm/fileattrs/* \
"

FILES:${PN}-sign = "\
    ${bindir}/rpmsign \
    ${libdir}/librpmsign.so.* \
"

FILES:${PN}-archive = "\
    ${bindir}/rpm2archive \
"

PACKAGES += "python3-rpm"
PROVIDES += "python3-rpm"
FILES:python3-rpm = "${PYTHON_SITEPACKAGES_DIR}/rpm/* ${PYTHON_SITEPACKAGES_DIR}/rpm-*.egg-info"

RDEPENDS:${PN}-build = "bash perl python3-core debugedit"

PACKAGE_PREPROCESS_FUNCS += "rpm_package_preprocess"

# Do not specify a sysroot when compiling on a target.
rpm_package_preprocess () {
	sed -i -e 's:--sysroot[^ ]*::g' \
	    ${PKGD}/${libdir}/rpm/macros
}

SSTATE_HASHEQUIV_FILEMAP = " \
    populate_sysroot:*/rpm/macros:${TMPDIR} \
    populate_sysroot:*/rpm/macros:${COREBASE} \
    "
