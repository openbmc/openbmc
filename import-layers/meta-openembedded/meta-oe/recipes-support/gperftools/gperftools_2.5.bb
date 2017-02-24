SUMMARY = "Fast, multi-threaded malloc() and nifty performance analysis tools"
HOMEPAGE = "http://code.google.com/p/gperftools/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=762732742c73dc6c7fbe8632f06c059a"
DEPENDS = "libunwind"

SRC_URI = "http://pkgs.fedoraproject.org/repo/pkgs/gperftools/gperftools-2.5.tar.gz/aa1eaf95dbe2c9828d0bd3a00f770f50/gperftools-2.5.tar.gz"

SRC_URI[md5sum] = "aa1eaf95dbe2c9828d0bd3a00f770f50"
SRC_URI[sha256sum] = "6fa2748f1acdf44d750253e160cf6e2e72571329b42e563b455bde09e9e85173"

inherit autotools

# On mips, we have the following error.
#   do_page_fault(): sending SIGSEGV to ls for invalid read access from 00000008
#   Segmentation fault (core dumped)
COMPATIBLE_HOST = "(i.86|x86_64|powerpc|powerpc64|arm|aarch64).*-linux*"
# On aarch64, add this option to avoid system hanging when using libtcmalloc.so.
EXTRA_OECONF_aarch64 += "--disable-libunwind"
