SUMMARY = "tool for automating interactive applications according to a script"
DESCRIPTION = "Expect is a tool for automating interactive applications according to a script. \
Following the script, Expect knows what can be expected from a program and what \
the correct response should be. Expect is also useful for testing these same \
applications. And by adding Tk, you can also wrap interactive applications in \
X11 GUIs. An interpreted language provides branching and high-level control \
structures to direct the dialogue. In addition, the user can take control and \
interact directly when desired, afterward returning control to the script. \
"
HOMEPAGE = "http://sourceforge.net/projects/expect/"
LICENSE = "PD"
SECTION = "devel"

LIC_FILES_CHKSUM = "file://license.terms;md5=fbf2de7e9102505b1439db06fc36ce5c"

DEPENDS += "tcl"
RDEPENDS:${PN} = "tcl"

inherit autotools update-alternatives ptest

SRC_URI = "${SOURCEFORGE_MIRROR}/expect/Expect/${PV}/${BPN}${PV}.tar.gz \
           file://0001-configure.in.patch \
           file://0002-tcl.m4.patch \
           file://0001-expect-install-scripts-without-using-the-fixline1-tc.patch \
           file://0001-Resolve-string-formatting-issues.patch \
           file://0001-expect-Fix-segfaults-if-Tcl-is-built-with-stubs-and-.patch \
           file://0001-exp_main_sub.c-Use-PATH_MAX-for-path.patch \
           file://0001-fixline1-fix-line-1.patch \
           file://0001-Add-prototype-to-function-definitions.patch \
           file://run-ptest \
           "
SRC_URI[md5sum] = "00fce8de158422f5ccd2666512329bd2"
SRC_URI[sha256sum] = "49a7da83b0bdd9f46d04a04deec19c7767bb9a323e40c4781f89caf760b92c34"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/expect/files/Expect/"
UPSTREAM_CHECK_REGEX = "/Expect/(?P<pver>(\d+[\.\-_]*)+)/"

S = "${WORKDIR}/${BPN}${PV}"

do_install:append() {
	install -d ${D}${libdir}
        install -m 0755 ${D}${libdir}/expect${PV}/libexpect*.so   ${D}${libdir}/
        install -m 0755 ${S}/fixline1           ${D}${libdir}/expect${PV}/
        rm ${D}${libdir}/expect${PV}/libexpect*.so
        sed -e 's|$dir|${libdir}|' -i ${D}${libdir}/expect${PV}/pkgIndex.tcl
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    cp -r ${S}/tests ${D}${PTEST_PATH}
}

# Apparently the public Tcl headers are only in /usr/include/tcl8.6
# when building for the target and nativesdk.
TCL_INCLUDE_PATH = "--with-tclinclude=${STAGING_INCDIR}/tcl8.6"
TCL_INCLUDE_PATH:class-native = ""

EXTRA_OECONF += "--with-tcl=${STAGING_LIBDIR} \
                 --enable-shared \
                 --enable-threads \
                 --disable-rpath \
                 ${TCL_INCLUDE_PATH} \
                "
EXTRA_OEMAKE_install = " 'SCRIPTS=' "

ALTERNATIVE:${PN}  = "mkpasswd"
ALTERNATIVE_LINK_NAME[mkpasswd] = "${bindir}/mkpasswd"
# Use lower priority than busybox's mkpasswd (created when built with CONFIG_CRYPTPW)
ALTERNATIVE_PRIORITY[mkpasswd] = "40"

FILES:${PN}-dev = "${libdir_native}/expect${PV}/libexpect*.so \
                   ${includedir}/expect.h \
                   ${includedir}/expect_tcl.h \
                   ${includedir}/expect_comm.h \
                   ${includedir}/tcldbg.h \
                   ${includedir}/*.h \
                  "

FILES:${PN} += "${libdir}/libexpect${PV}.so \
                ${libdir}/expect${PV}/* \
               "

BBCLASSEXTEND = "native nativesdk"
