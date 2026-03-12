SUMMARY = "LLD - the LLVM Linker"
DESCRIPTION = "LLD is a linker from the LLVM project that is a drop-in replacement for system linkers and runs much faster than them."
HOMEPAGE = "https://lld.llvm.org"
SECTION = "devel"

require common-clang.inc
require common-source.inc

LIC_FILES_CHKSUM = "file://lld/LICENSE.TXT;md5=ae7dc7c027b1fa89b5b013d391d3ee2b"

inherit cmake pkgconfig

DEPENDS = "llvm-tblgen-native llvm"

OECMAKE_SOURCEPATH = "${S}/lld"

# Explicitly enable symlinks as the lld build doesn't call into the llvm setup
# and turn that on.
EXTRA_OECMAKE += "-DLLVM_INCLUDE_TESTS=OFF -DLLVM_USE_SYMLINKS=ON \
                  -DLLVM_TABLEGEN_EXE=${STAGING_BINDIR_NATIVE}/llvm-tblgen"

BBCLASSEXTEND = "native nativesdk"
