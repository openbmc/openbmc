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
RDEPENDS_${PN} = "tcl"

inherit autotools

PR = "r1"

SRC_URI = "${SOURCEFORGE_MIRROR}/expect/Expect/${PV}/${BPN}${PV}.tar.gz \
           file://0001-configure.in.patch \
           file://0002-tcl.m4.patch \
           file://01-example-shebang.patch \
           file://0001-expect-install-scripts-without-using-the-fixline1-tc.patch \
          "
SRC_URI[md5sum] = "44e1a4f4c877e9ddc5a542dfa7ecc92b"
SRC_URI[sha256sum] = "b28dca90428a3b30e650525cdc16255d76bb6ccd65d448be53e620d95d5cc040"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/expect/files/Expect/"
UPSTREAM_CHECK_REGEX = "/Expect/(?P<pver>(\d+[\.\-_]*)+)/"

S = "${WORKDIR}/${BPN}${PV}"

do_install_append() {
	install -d ${D}${libdir}
        install -m 0755 ${D}${libdir}/expect${PV}/libexpect*.so   ${D}${libdir}/
        install -m 0755 ${S}/fixline1           ${D}${libdir}/expect${PV}/
        install -m 0755 ${S}/example/*          ${D}${libdir}/expect${PV}/
        rm ${D}${libdir}/expect${PV}/libexpect*.so
        sed -e 's|$dir|${libdir}|' -i ${D}${libdir}/expect${PV}/pkgIndex.tcl
}

EXTRA_OECONF += "--with-tcl=${STAGING_LIBDIR} \
                 --with-tclinclude=${STAGING_INCDIR}/tcl8.6 \
                 --enable-shared \
                 --enable-threads \
                 --disable-rpath \
                "
EXTRA_OEMAKE_install = " 'SCRIPTS=' "

FILES_${PN}-dev = "${libdir_native}/expect${PV}/libexpect*.so \
                   ${includedir}/expect.h \
                   ${includedir}/expect_tcl.h \
                   ${includedir}/expect_comm.h \
                   ${includedir}/tcldbg.h \
                   ${includedir}/*.h \
                  "

FILES_${PN} += "${libdir}/libexpect${PV}.so \
                ${libdir}/expect${PV}/* \
               "
