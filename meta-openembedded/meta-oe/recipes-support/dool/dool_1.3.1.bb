SUMMARY = "Dool is a Python3 compatible fork of Dstat"
DESCRIPTION = "Dool is a command line tool to monitor many aspects of your system: \
CPU, Memory, Network, Load Average, etc. It also includes a robust plug-in architecture \
to allow monitoring other system metrics."
HOMEPAGE = "http://dag.wiee.rs/home-made/dstat"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

DEPENDS += "asciidoc-native xmlto-native"
RDEPENDS:${PN} += "python3-core python3-misc python3-resource python3-shell python3-six python3-unixadmin"

SRC_URI = "git://github.com/scottchiefbaker/dool.git;branch=master;protocol=https \
          "

SRCREV = "30847c748483bc088549486292232d805b086b1f"

S = "${WORKDIR}/git"

do_install() {
    oe_runmake 'DESTDIR=${D}' install
}


