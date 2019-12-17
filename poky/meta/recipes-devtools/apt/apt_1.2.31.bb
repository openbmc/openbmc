DEPENDS = "curl db zlib"
RDEPENDS_${PN} = "dpkg bash debianutils"
require apt.inc

require apt-package.inc

PACKAGECONFIG ??= "lzma"
PACKAGECONFIG[lzma] = "ac_cv_lib_lzma_lzma_easy_encoder=yes,ac_cv_lib_lzma_lzma_easy_encoder=no,xz"
PACKAGECONFIG[bz2] = "ac_cv_lib_bz2_BZ2_bzopen=yes,ac_cv_lib_bz2_BZ2_bzopen=no,bzip2"
PACKAGECONFIG[lz4] = "ac_cv_lib_lz4_LZ4F_createCompressionContext=yes,ac_cv_lib_lz4_LZ4F_createCompressionContext=no,lz4"

FILES_${PN} += "${bindir}/apt-key"
apt-manpages += "doc/apt-key.8"

do_install_append() {
    #Write the correct apt-architecture to apt.conf
    APT_CONF=${D}/etc/apt/apt.conf
    echo 'APT::Architecture "${DPKG_ARCH}";' > ${APT_CONF}
    oe_libinstall -so -C bin libapt-private ${D}${libdir}/
}
