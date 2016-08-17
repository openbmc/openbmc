SUMMARY = "Ninja is a small build system with a focus on speed."
HOMEPAGE = "http://martine.github.com/ninja/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=a81586a64ad4e476c791cda7e2f2c52e"

DEPENDS = "re2c-native"

inherit native

SRCREV="484c16336f19bd8970bb6e75322d61b92a229899"

SRC_URI = "git://github.com/martine/ninja.git;branch=release"

S="${WORKDIR}/git"

do_configure[noexec] = "1"

do_compile() {
    python ${S}/configure.py --bootstrap
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/ninja ${D}${bindir}/
}
