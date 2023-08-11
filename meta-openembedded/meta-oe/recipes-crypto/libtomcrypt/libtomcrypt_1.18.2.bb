SUMMARY = "LibTomCrypt is a public domain open source cryptographic toolkit"
HOMEPAGE = "https://www.libtom.net/LibTomCrypt"
SECTION = "libs"
# Unlicense isn't very accurate for this revision, it was WTFPL in 0.18.0-rc1:
# https://github.com/libtom/libtomcrypt/commit/77e31fb6a980212e90b9a50f116dc5a7bd91e527
# then updated to dual license PD and WTFPL also in 0.18.0-rc1:
# https://github.com/libtom/libtomcrypt/commit/412b2ee1fccc3a0df58f93f372c90d6d0f93bfc9
# and then updated again to Unlicense after the 0.18.2 tag (it's only in develop branch):
# https://github.com/libtom/libtomcrypt/commit/3630bee6fc0f73dd9c7923fd43f8ae15a2c0fb70
# but keep using Unlicense to avoid triggering people with WTFPL license:
# https://groups.google.com/g/libtom/c/17Z7xkECULM
# and this comment can be removed next time libtomcrypt is updated
LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=71baacc459522324ef3e2b9e052e8180"

DEPENDS = "libtool-cross"

SRC_URI = "git://github.com/libtom/libtomcrypt.git;protocol=https;branch=master \
   file://CVE-2019-17362.patch \
"

SRCREV = "7e7eb695d581782f04b24dc444cbfde86af59853"

S = "${WORKDIR}/git"

inherit pkgconfig

PACKAGECONFIG ??= "ltm"
PACKAGECONFIG[ltm] = ",,libtommath"

CFLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'ltm', '-DUSE_LTM -DLTM_DESC', '', d)}"

EXTRA_OEMAKE = "'PREFIX=${prefix}' 'DESTDIR=${D}' 'LIBPATH=${libdir}' 'CFLAGS=${CFLAGS}'"

do_compile() {
    oe_runmake -f makefile.shared
}

do_install() {
    oe_runmake -f makefile.shared install
}
