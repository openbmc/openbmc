SUMMARY = "LLDB"
DESCRIPTION = "LLDB is a next generation, high-performance debugger."
HOMEPAGE = "https://lldb.llvm.org"
SECTION = "devel"

require common-clang.inc
require common-source.inc

COMPATIBLE_HOST = "(x86_64|i.86|arm|aarch64|powerpc64).*-linux"

LIC_FILES_CHKSUM = "file://lldb/LICENSE.TXT;md5=2e0d44968471fcde980034dbb826bea9"

inherit cmake pkgconfig python3native python3targetconfig

# This actually just depends on LLVM but right now llvm and clang are built together
DEPENDS = "llvm-tblgen-native clang"

OECMAKE_SOURCEPATH = "${S}/lldb"

EXTRA_OECMAKE = "-DLLDB_INCLUDE_TESTS=OFF \
                 -DLLDB_ENABLE_LUA=OFF \
                 -DLLDB_PYTHON_RELATIVE_PATH=${PYTHON_SITEPACKAGES_DIR} \
                 -DLLDB_PYTHON_EXE_RELATIVE_PATH=${PYTHON_PN} \
                 -DLLDB_PYTHON_EXT_SUFFIX=${SOLIBSDEV} \
                 -DLLVM_DIR=${STAGING_LIBDIR}/cmake/llvm/ \
                 -DLLVM_TABLEGEN=${STAGING_BINDIR_NATIVE}/llvm-tblgen \
                 -DLLDB_TABLEGEN_EXE=${STAGING_BINDIR_NATIVE}/lldb-tblgen \
"

PACKAGECONFIG ??= "libedit libxml2 lzma"
PACKAGECONFIG[libedit] = "-DLLDB_ENABLE_LIBEDIT=ON,-DLLDB_ENABLE_LIBEDIT=OFF,libedit"
PACKAGECONFIG[libxml2] = "-DLLDB_ENABLE_LIBXML2=ON,-DLLDB_ENABLE_LIBXML2=OFF,libxml2"
PACKAGECONFIG[lzma] = "-DLLDB_ENABLE_LZMA=ON,-DLLDB_ENABLE_LZMA=OFF,xz"
PACKAGECONFIG[python] = "-DLLDB_ENABLE_PYTHON=ON,-DLLDB_ENABLE_PYTHON=OFF,swig-native"

do_install:append() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'python', 'true', 'false', d)}; then
        # Fix a bad symlink that points to the -dev .so
        rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/lldb/_lldb.so
        ln -s ${libdir}/liblldb.so.${MAJOR_VER}.${MINOR_VER} ${D}${PYTHON_SITEPACKAGES_DIR}/lldb/_lldb.so
    fi
}

PACKAGES =+ "${PN}-python ${PN}-server"

FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"
RDEPENDS:${PN}-python = "${PN}"
# This has a symlink to the lldb library
INSANE_SKIP:${PN}-python = "dev-so"

FILES:${PN}-server = "${bindir}/lldb-server"

BBCLASSEXTEND = "native nativesdk"
