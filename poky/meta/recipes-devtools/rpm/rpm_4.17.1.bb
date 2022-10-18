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

SRC_URI = "git://github.com/rpm-software-management/rpm;branch=rpm-4.17.x;protocol=https \
           file://environment.d-rpm.sh \
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
           file://0001-tools-Add-error.h-for-non-glibc-case.patch \
           file://0001-docs-do-not-build-manpages-requires-pandoc.patch \
           file://0001-build-pack.c-do-not-insert-payloadflags-into-.rpm-me.patch \
           file://0001-configure.ac-add-linux-gnux32-variant-to-triplet-han.patch \
           "

PE = "1"
SRCREV = "5bef402da334595ed9302b8bca1acdf5e88bfe11"

S = "${WORKDIR}/git"

DEPENDS = "lua libgcrypt file popt xz bzip2 elfutils python3"
DEPENDS:append:class-native = " file-replacement-native bzip2-replacement-native"

inherit autotools gettext pkgconfig python3native
export PYTHON_ABI

AUTOTOOLS_AUXDIR = "${S}/build-aux"

# OE-core patches autoreconf to additionally run gnu-configize, which fails with this recipe
EXTRA_AUTORECONF:append = " --exclude=gnu-configize"

# Vendor is detected differently on x86 and aarch64 hosts and can feed into target packages
EXTRA_OECONF:append = " --enable-python --with-crypto=libgcrypt --with-vendor=pc"
EXTRA_OECONF:append:libc-musl = " --disable-nls --disable-openmp"

# --sysconfdir prevents rpm from attempting to access machine-specific configuration in sysroot/etc; we need to have it in rootfs
# --localstatedir prevents rpm from writing its database to native sysroot when building images
# Forcibly disable plugins for native/nativesdk, as the inhibit and prioreset
# plugins both behave badly inside builds.
EXTRA_OECONF:append:class-native = " --sysconfdir=/etc --localstatedir=/var --disable-plugins"
EXTRA_OECONF:append:class-nativesdk = " --sysconfdir=/etc --disable-plugins"

BBCLASSEXTEND = "native nativesdk"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'inhibit', '', d)} sqlite zstd"
# The inhibit plugin serves no purpose outside of the target
PACKAGECONFIG:remove:class-native = "inhibit"
PACKAGECONFIG:remove:class-nativesdk = "inhibit"

PACKAGECONFIG[imaevm] = "--with-imaevm,,ima-evm-utils"
PACKAGECONFIG[inhibit] = "--enable-inhibit-plugin,--disable-inhibit-plugin,dbus"
PACKAGECONFIG[rpm2archive] = "--with-archive,--without-archive,libarchive"
PACKAGECONFIG[sqlite] = "--enable-sqlite=yes,--enable-sqlite=no,sqlite3"
PACKAGECONFIG[ndb] = "--enable-ndb,--disable-ndb"
PACKAGECONFIG[bdb-ro] = "--enable-bdb-ro,--disable-bdb-ro"
PACKAGECONFIG[zstd] = "--enable-zstd=yes,--enable-zstd=no,zstd"

ASNEEDED = ""

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

do_configure:prepend() {
        mkdir -p ${S}/build-aux
}

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
        for tool in ${WRAPPER_TOOLS}; do
                test -x ${D}$tool && create_wrapper ${D}$tool \
                        RPM_CONFIGDIR='`dirname $''realpath`'/${@os.path.relpath(d.getVar('libdir'), d.getVar('bindir'))}/rpm \
                        RPM_ETCCONFIGDIR='$'{RPM_ETCCONFIGDIR-'`dirname $''realpath`'/${@os.path.relpath(d.getVar('sysconfdir'), d.getVar('bindir'))}/..} \
                        MAGIC='`dirname $''realpath`'/${@os.path.relpath(d.getVar('datadir'), d.getVar('bindir'))}/misc/magic.mgc \
                        RPM_NO_CHROOT_FOR_SCRIPTS=1
        done

        rm -rf ${D}/var

        mkdir -p ${D}${SDKPATHNATIVE}/environment-setup.d
        install -m 644 ${WORKDIR}/environment.d-rpm.sh ${D}${SDKPATHNATIVE}/environment-setup.d/rpm.sh
}

# Rpm's make install creates var/tmp which clashes with base-files packaging
do_install:append:class-target() {
    rm -rf ${D}/var
}
do_install:append:class-nativesdk() {
    rm -rf ${D}${SDKPATHNATIVE}/var
}

do_install:append () {
	sed -i -e 's:${HOSTTOOLS_DIR}/::g' \
	    ${D}/${libdir}/rpm/macros

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
    ${libdir}/rpm/debugedit \
    ${libdir}/rpm/sepdebugcrcfix \
    ${libdir}/rpm/find-debuginfo.sh \
    ${libdir}/rpm/find-lang.sh \
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
FILES:python3-rpm = "${PYTHON_SITEPACKAGES_DIR}/rpm/*"

RDEPENDS:${PN}-build = "bash perl python3-core"

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
