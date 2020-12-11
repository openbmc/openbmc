SUMMARY = "FastCGI wrapper for CGI scripts"
DESCRIPTION = "FcgiWrap is a simple server for running CGI applications over FastCGI. Fcgiwrap can be used together with Nginx to serve CGI or Perl scripts"
HOMEPAGE = "https://github.com/gnosek/fcgiwrap"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a95d02d614a3a0232d4e6e51b7963c5b"

DEPENDS = "fcgi"

SRC_URI = "git://github.com/gnosek/fcgiwrap.git;protocol=https;branch=${BRANCH} \
           file://0001-Fix-implicit-fallthrough-warning.patch \
           "
BRANCH = "master"
SRCREV = "99c942c90063c73734e56bacaa65f947772d9186"

S = "${WORKDIR}/git"
CFLAGS =+ "-I${B}"
EXTRA_OEMAKE = "VPATH=${S}"

inherit autotools pkgconfig

do_install() {
    install -Dm 755 ${B}/fcgiwrap ${D}${sbindir}/fcgiwrap
}
