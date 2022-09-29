require atp-source_3.1.inc
inherit pkgconfig native

SUMMARY = "AMBA ATP Engine: synthetic traffic interface modelling framework"

S = "${WORKDIR}/git"
SRC_URI = "${ATP_SRC} \
           file://no-werror.patch"

EXTRA_OEMAKE += "EXTRA_CXX_FLAGS='${CXXFLAGS}' EXTRA_LD_FLAGS='${LDFLAGS}'"

do_install() {
    oe_runmake install
}

DEPENDS = "protobuf-native cppunit-native"

addtask addto_recipe_sysroot before do_build
