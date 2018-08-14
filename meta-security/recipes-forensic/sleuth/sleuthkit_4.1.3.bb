SUMMARY = "The Sleuth Kit (TSK) is a library and collection of command line tools that allow you to investigate disk images."
HOMEPAGE = "http://www.sleuthkit.org/sleuthkit/"
LICENSE = "IPL-1.0 & GPLv2 & CPL-1.0"
LIC_FILES_CHKSUM = "file://licenses/GNU-COPYING;startline=4;endline=5;md5=475b4784903850b579dc6e6310bd5f08\
    file://licenses/IBM-LICENSE;startline=1;endline=2;md5=1fc3300388b0d6e6216825dd89c2e3a2\
    file://licenses/cpl1.0.txt;startline=1;endline=2;md5=9e58c878202c73a4e3ed4be72598fb92"

DEPENDS = "libtool"

SRC_URI = "http://archive.ubuntu.com/ubuntu/pool/universe/s/${BPN}/${BPN}_${PV}.orig.tar.gz;name=orig \
            file://fix_host_poison.patch \
        "
SRC_URI[orig.md5sum] = "139a12f06952d8a40bbe07884994cf5d"
SRC_URI[orig.sha256sum] = "67f9d2a31a8884d58698d6122fc1a1bfa9bf238582bde2b49228ec9b899f0327"

inherit autotools-brokensep pkgconfig gettext

PACKAGECONFIG ??= "aff zlib ewf"
PACKAGECONFIG[aff] = "--with-afflib=${STAGING_DIR_HOST}/usr, --without-afflib, afflib"
PACKAGECONFIG[zlib] = "--with-zlib=${STAGING_DIR_HOST}/usr, --without-zlib, zlib"
PACKAGECONFIG[ewf] = "--with-libewf=${STAGING_DIR_HOST}/usr, --without-libewf, libewf"

#--with-gnu-ld
EXTRA_OECONF += "--enable-static=no --disable-java LIBS='-L${STAGING_LIBDIR}' LDFLAGS='-L${STAGING_LIBDIR}' CPPFLAGS='-I${STAGING_INCDIR}'"

# Avoid QA Issue: No GNU_HASH in the elf binary
INSANE_SKIP_${PN} = "ldflags" 

FILES_${PN} += " ${datadir}/tsk"

RDEPENDS_${PN} += " perl"
