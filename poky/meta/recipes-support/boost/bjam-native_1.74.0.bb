require boost-${PV}.inc

SUMMARY = "Portable Boost.Jam build tool for boost"
SECTION = "devel"

inherit native

SRC_URI += "file://0001-Build-debug-version-of-bjam.patch \
            file://0001-build.sh-use-DNDEBUG-also-in-debug-builds.patch \
           "

do_compile() {
    ./bootstrap.sh --with-toolset=gcc
}

do_install() {
    install -d ${D}${bindir}/
    # install unstripped version for bjam
    install -c -m 755 b2 ${D}${bindir}/bjam
}
