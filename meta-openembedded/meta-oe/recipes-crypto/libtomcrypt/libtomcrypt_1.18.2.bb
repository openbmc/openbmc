SUMMARY = "LibTomCrypt is a public domain open source cryptographic toolkit"
HOMEPAGE = "https://www.libtom.net/LibTomCrypt"
SECTION = "libs"
LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=71baacc459522324ef3e2b9e052e8180"

DEPENDS += "libtool-cross"

SRC_URI = "git://github.com/libtom/libtomcrypt.git;protocol=https;branch=master"

SRCREV = "7e7eb695d581782f04b24dc444cbfde86af59853"

S = "${WORKDIR}/git"

do_compile() {
    oe_runmake -f makefile.shared
}

do_install() {
    oe_runmake -f makefile.shared 'PREFIX=${prefix}' 'DESTDIR=${D}' install
}
