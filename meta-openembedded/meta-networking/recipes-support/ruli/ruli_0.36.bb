SUMMARY = "RULI stands for Resolver User Layer Interface It's a library	built on top of an asynchronous DNS stub resolver"

HOMEPAGE = "http://www.nongnu.org/ruli/"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

DEPENDS = "liboop"

SRC_URI = "http://download.savannah.gnu.org/releases/ruli/ruli_${PV}.orig.tar.gz \
           file://0001-Fix-build-with-format-string-checks.patch \
           file://0001-src-ruli_addr.c-Add-missing-format-string.patch \
           file://0001-ruli_srv-Mark-prev_addr_list_size-as-unused.patch \
           file://0001-Make-space-for-flags-from-environment.patch \
           file://float-conversion.patch \
           "

SRC_URI[md5sum] = "e73fbfdeadddb68a703a70cea5271468"
SRC_URI[sha256sum] = "11d32def5b514748fbd9ea8c88049ae99e1bb358efc74eb91a4d268a3999dbfa"

EXTRA_OEMAKE = 'CC="${CC}" OOP_BASE_DIR="${STAGING_EXECPREFIXDIR}" \
                INSTALL_BASE_DIR="${D}${exec_prefix}" \
                OOP_LIB_DIR=${STAGING_EXECPREFIXDIR}/${baselib} \
                INSTALL_LIB_DIR=${D}${libdir}'

do_configure() {
    touch configure-stamp
}

do_install() {
    oe_runmake install
}

PACKAGES =+ "${PN}-bin"
