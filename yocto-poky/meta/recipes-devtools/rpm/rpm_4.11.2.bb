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

SUMMARY_python-rpm = "Python bindings for apps which will manupulate RPM packages"
DESCRIPTION_python-rpm = "The rpm-python package contains a module that permits applications \
written in the Python programming language to use the interface \
supplied by the RPM Package Manager libraries."

HOMEPAGE = "http://www.rpm.org"
LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM ??= "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

DEPENDS = "db libxml2 xz findutils file popt nss bzip2 elfutils attr zlib acl gzip python"
DEPENDS_append_class-native = " file-replacement-native"

SRC_URI += "http://rpm.org/releases/rpm-4.11.x/${BP}.tar.bz2 \
            file://use-pkgconfig-for-python.patch \
            file://remove-db3-from-configure.patch \
            file://add_RPMSENSE_MISSINGOK_to_rpmmodule.patch \
            file://support-suggests-tag.patch \
            file://remove-dir-check.patch \
            file://disable_shortcircuited.patch \
            file://fix_libdir.patch \
            file://rpm-scriptetexechelp.patch \
            file://pythondeps.sh \
            file://rpm-CVE-2014-8118.patch \
            file://rpm-CVE-2013-6435.patch \
           "

SRC_URI[md5sum] = "876ac9948a88367054f8ddb5c0e87173"
SRC_URI[sha256sum] = "403f8de632b33846ce5746f429c21a60f40dff9dcb56f1b4118f37a0652a48d4"

PR = "r1"

inherit autotools
inherit pythonnative
inherit pkgconfig
inherit gettext

EXTRA_OECONF += "--host=${HOST_SYS} \
                 --program-prefix= \
                 --prefix=${prefix} \
                 --exec-prefix=${prefix} \
                 --bindir=${prefix}/bin \
                 --sbindir=${prefix}/sbin \
                 --sysconfdir=${sysconfdir} \
                 --datadir=${prefix}/share \
                 --includedir=${prefix}/include \
                 --libdir=${prefix}/lib \
                 --libexecdir=${prefix}/libexec \
                 --localstatedir=${localstatedir} \
                 --sharedstatedir=${prefix}/com \
                 --mandir=${mandir} \
                 --infodir=${infodir} \
                 --disable-dependency-tracking \
                 --with-acl \
                 --without-lua \
                 --without-cap \
                 --enable-shared \
                 --enable-python \
                 --with-external-db \
                "

CPPFLAGS_append = " `pkg-config --cflags nss`"
LDFLAGS_append = " -Wl,-Bsymbolic-functions -ffunction-sections"
CCFLAGS_append = " -fPIC "
CXXFLAGS_append = " -fPIC "
CFLAGS_append = " -fPIC -DRPM_VENDOR_WINDRIVER -DRPM_VENDOR_POKY -DRPM_VENDOR_OE "

do_configure_prepend() {
    rm -rf sqlite
    rm -f m4/libtool.m4
    rm -f m4/lt*.m4
    rm -rf db3/configure*
}

do_install_append() {
    mv ${D}/${base_bindir}/rpm ${D}/${bindir}/
    rmdir ${D}/${base_bindir}
    rm -f ${D}${prefix}/lib/*.la
    rm -f ${D}${prefix}/lib/rpm-plugins/*.la
    rm -f ${D}/${libdir}/python%{with_python_version}/site-packages/*.{a,la}
    rm -f ${D}/${libdir}/python%{with_python_version}/site-packages/rpm/*.{a,la}
    rm -fr ${D}/var
    install -d ${D}${prefix}/lib/rpm/bin
    ln -s ../debugedit ${D}${prefix}/lib/rpm/bin/debugedit
    ln -s ../rpmdeps ${D}${prefix}/lib/rpm/bin/rpmdeps-oecore
    install -m 0755 ${WORKDIR}/pythondeps.sh ${D}/${libdir}/rpm/pythondeps.sh
}

pkg_postinst_${PN}() {

    [ "x\$D" == "x" ] && ldconfig
    test -f ${localstatedir}/lib/rpm/Packages || rpm --initdb
    rm -f ${localstatedir}/lib/rpm/Filemd5s \
          ${localstatedir}/lib/rpm/Filedigests \
          ${localstatedir}/lib/rpm/Requireversion \
          ${localstatedir}/lib/rpm/Provideversion

}

pkg_postrm_${PN}() {
    [ "x\$D" == "x" ] && ldconfig

}

PACKAGES += "python-${PN}"
PROVIDES += "python-rpm"

FILES_${PN} +=  "${libdir}/rpm \
                 ${libdir}/rpm-plugins/exec.so \
                "
RDEPENDS_${PN} = "base-files run-postinsts"
RDEPENDS_${PN}_class-native = ""

FILES_${PN}-dbg += "${libdir}/rpm/.debug/* \
                    ${libdir}/rpm-plugins/.debug/* \
                    ${libdir}/python2.7/site-packages/rpm/.debug/* \
                   "

FILES_${PN}-dev += "${libdir}/python2.7/site-packages/rpm/*.la"

FILES_python-${PN} = "${libdir}/python2.7/site-packages/rpm/*"
RDEPENDS_python-${PN} = "${PN} python"

BBCLASSEXTEND = "native"
