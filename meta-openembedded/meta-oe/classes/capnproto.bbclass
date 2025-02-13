# Inherit this class in your recipe to compile against 
# Cap'N Proto (capnproto) with CMake

DEPENDS:append = " capnproto-native "
DEPENDS:append:class-target = " capnproto "

EXTRA_OECMAKE:append:class-target = " -DCAPNP_EXECUTABLE=${RECIPE_SYSROOT_NATIVE}${bindir}/capnp \
                                      -DCAPNPC_CXX_EXECUTABLE=${RECIPE_SYSROOT_NATIVE}${bindir}/capnpc-c++ "
