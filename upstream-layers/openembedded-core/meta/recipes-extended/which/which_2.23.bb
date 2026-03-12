SUMMARY = "Displays the full path of shell commands"
DESCRIPTION = "Which is a utility that prints out the full path of the \
executables that bash(1) would execute when the passed \
program names would have been entered on the shell prompt. \
It does this by using the exact same algorithm as bash."
SECTION = "libs"
HOMEPAGE = "https://carlowood.github.io/which/"

LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504\
                    file://which.c;beginline=1;endline=17;md5=a9963693af2272e7a8df6f231164e7a2"

inherit meson update-alternatives

SRC_URI = "${GNU_MIRROR}/which/which-${PV}.tar.gz \
           file://meson.build \
           file://0001-getopt-Fix-signature-of-getenv-function.patch \
           "

SRC_URI[sha256sum] = "a2c558226fc4d9e4ce331bd2fd3c3f17f955115d2c00e447618a4ef9978a2a73"

do_configure:prepend() {
       cp ${UNPACKDIR}/meson.build ${S}
}

ALTERNATIVE:${PN} = "which"
ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN}-doc = "which.1"
ALTERNATIVE_LINK_NAME[which.1] = "${mandir}/man1/which.1"

BBCLASSEXTEND = "nativesdk"
