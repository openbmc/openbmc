SUMMARY = "LLVM TableGen binaries, for the build host"
SECTION = "devel"

# Building the LLVM projects often means using the TableGen tools (llvm-tblgen,
# etc). We could build these as part of llvm-native, but there are cases where
# this would be overkill. For example, building a target LLDB needs native
# tablegen binaries and a target libllvm so having to build a complete native
# llvm is not needed.

require common-clang.inc
require common-source.inc

inherit cmake pkgconfig native

OECMAKE_SOURCEPATH = "${S}/llvm"

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release -DLLVM_ENABLE_PROJECTS='llvm;clang;lldb'"

OECMAKE_TARGET_COMPILE = "llvm-tblgen clang-tblgen lldb-tblgen"

do_install() {
    for TARGET in ${OECMAKE_TARGET_COMPILE}; do
        DESTDIR=${D} cmake --install ${B} --component $TARGET
    done
}
