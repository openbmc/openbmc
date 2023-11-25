SUMMARY = "Reparent a running program to a new terminal"
DESCRIPTION = "reptyr is a utility for taking an existing running program and \
attaching it to a new terminal. Started a long-running process over ssh, but \
have to leave and don't want to interrupt it? Just start a screen, use reptyr \
to grab it, and then kill the ssh session and head on home."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=25a0555028c71837623fa6dfa4cc45c0"

SRC_URI = "git://github.com/nelhage/reptyr.git;protocol=https;branch=master"
SRCREV = "1238097fc2cd15db058d2185cc4985daa87bcd41"

S = "${WORKDIR}/git"

inherit bash-completion github-releases manpages pkgconfig

GITHUB_BASE_URI = "https://github.com/nelhage/${BPN}/releases/"

PACKAGECONFIG ?= ""
PACKAGECONFIG[manpages] = ""

EXTRA_OEMAKE = "'BINDIR=${bindir}' 'MANDIR=${mandir}' 'PKG_CONFIG=${STAGING_BINDIR_NATIVE}/pkg-config'"

do_compile () {
	oe_runmake
}

do_install () {
	oe_runmake install 'DESTDIR=${D}'
}
