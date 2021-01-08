SUMMARY = "The RPM package management system"
DESCRIPTION = "The RPM Package Manager (RPM) is a powerful command line driven \
package management system capable of installing, uninstalling, \
verifying, querying, and updating software packages. Each software \
package consists of an archive of files along with information about \
the package like its version, a description, etc."

SUMMARY_${PN}-dev = "Development files for manipulating RPM packages"
DESCRIPTION_${PN}-dev = "This package contains the RPM C library and header files. These \
development files will simplify the process of writing programs that \
manipulate RPM packages and databases. These files are intended to \
simplify the process of creating graphical package managers or any \
other tools that need an intimate knowledge of RPM packages in order \
to function."

SUMMARY_python3-rpm = "Python bindings for apps which will manupulate RPM packages"
DESCRIPTION_python3-rpm = "The python3-rpm package contains a module that permits applications \
written in the Python programming language to use the interface \
supplied by the RPM Package Manager libraries."

HOMEPAGE = "http://www.rpm.org"

# libraries are also LGPL - how to express this?
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=c4eec0c20c6034b9407a09945b48a43f"

SRC_URI = "git://github.com/rpm-software-management/rpm;branch=rpm-4.16.x \
           file://environment.d-rpm.sh \
           file://0001-Do-not-add-an-unsatisfiable-dependency-when-building.patch \
           file://0001-Do-not-read-config-files-from-HOME.patch \
           file://0001-When-cross-installing-execute-package-scriptlets-wit.patch \
           file://0001-Do-not-reset-the-PATH-environment-variable-before-ru.patch \
           file://0002-Add-support-for-prefixing-etc-from-RPM_ETCCONFIGDIR-.patch \
           file://0001-Do-not-hardcode-lib-rpm-as-the-installation-path-for.patch \
           file://0001-Fix-build-with-musl-C-library.patch \
           file://0001-Add-a-color-setting-for-mips64_n32-binaries.patch \
           file://0011-Do-not-require-that-ELF-binaries-are-executable-to-b.patch \
           file://0001-perl-disable-auto-reqs.patch \
           file://0001-rpm-rpmio.c-restrict-virtual-memory-usage-if-limit-s.patch \
           file://0016-rpmscript.c-change-logging-level-around-scriptlets-t.patch \
           file://0001-lib-transaction.c-fix-file-conflicts-for-MIPS64-N32.patch \
           file://0001-tools-Add-error.h-for-non-glibc-case.patch \
           "

PE = "1"
SRCREV = "278883a704ea36c97974d0f2d65d41abe78b0e2a"

S = "${WORKDIR}/git"

DEPENDS = "libgcrypt db file popt xz bzip2 elfutils python3"
DEPENDS_append_class-native = " file-replacement-native bzip2-replacement-native"

inherit autotools gettext pkgconfig python3native
export PYTHON_ABI

AUTOTOOLS_AUXDIR = "${S}/build-aux"

# OE-core patches autoreconf to additionally run gnu-configize, which fails with this recipe
EXTRA_AUTORECONF_append = " --exclude=gnu-configize"

EXTRA_OECONF_append = " --without-lua --enable-python --with-crypto=libgcrypt"
EXTRA_OECONF_append_libc-musl = " --disable-nls --disable-openmp"

# --sysconfdir prevents rpm from attempting to access machine-specific configuration in sysroot/etc; we need to have it in rootfs
# --localstatedir prevents rpm from writing its database to native sysroot when building images
# Forcibly disable plugins for native/nativesdk, as the inhibit and prioreset
# plugins both behave badly inside builds.
EXTRA_OECONF_append_class-native = " --sysconfdir=/etc --localstatedir=/var --disable-plugins"
EXTRA_OECONF_append_class-nativesdk = " --sysconfdir=/etc --disable-plugins"

BBCLASSEXTEND = "native nativesdk"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'inhibit', '', d)}"
# The inhibit plugin serves no purpose outside of the target
PACKAGECONFIG_remove_class-native = "inhibit"
PACKAGECONFIG_remove_class-nativesdk = "inhibit"

PACKAGECONFIG[imaevm] = "--with-imaevm,,ima-evm-utils"
PACKAGECONFIG[inhibit] = "--enable-inhibit-plugin,--disable-inhibit-plugin,dbus"
PACKAGECONFIG[rpm2archive] = "--with-archive,--without-archive,libarchive"

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

do_configure_prepend() {
        mkdir -p ${S}/build-aux
}

do_install_append_class-native() {
        for tool in ${WRAPPER_TOOLS}; do
                test -x ${D}$tool && create_wrapper ${D}$tool \
                        RPM_CONFIGDIR=${STAGING_LIBDIR_NATIVE}/rpm \
                        RPM_ETCCONFIGDIR=${STAGING_DIR_NATIVE} \
                        MAGIC=${STAGING_DIR_NATIVE}${datadir_native}/misc/magic.mgc \
                        RPM_NO_CHROOT_FOR_SCRIPTS=1
        done
}

do_install_append_class-nativesdk() {
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
do_install_append_class-target() {
    rm -rf ${D}/var
}

do_install_append () {
	sed -i -e 's:${HOSTTOOLS_DIR}/::g' \
	    ${D}/${libdir}/rpm/macros

	sed -i -e 's|/usr/bin/python|${USRBINPATH}/env ${PYTHON_PN}|' \
	    ${D}${libdir}/rpm/pythondistdeps.py
}

FILES_${PN} += "${libdir}/rpm-plugins/*.so \
               "
FILES_${PN}_append_class-nativesdk = " ${SDKPATHNATIVE}/environment-setup.d/rpm.sh"

FILES_${PN}-dev += "${libdir}/rpm-plugins/*.la \
                    "

PACKAGES += "python3-rpm"
PROVIDES += "python3-rpm"
FILES_python3-rpm = "${PYTHON_SITEPACKAGES_DIR}/rpm/*"

# rpm 5.x was packaging the rpm build tools separately
RPROVIDES_${PN} += "rpm-build"

RDEPENDS_${PN} = "bash perl python3-core"

PACKAGE_PREPROCESS_FUNCS += "rpm_package_preprocess"

# Do not specify a sysroot when compiling on a target.
rpm_package_preprocess () {
	sed -i -e 's:--sysroot[^ ]*::g' \
	    ${PKGD}/${libdir}/rpm/macros
}
