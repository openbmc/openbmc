SUMMARY = "tcpslice"
DESCRIPTION = "A tool for extracting parts of a tcpdump packet trace."
HOMEPAGE = "http://www.tcpdump.org/related.html"
SECTION = "net"

LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://tcpslice.c;endline=20;md5=99519e2e5234d1662a4ce16baa62c64e"

SRC_URI = "ftp://ftp.ee.lbl.gov/${BP}.tar.gz \
           file://tcpslice-1.2a3-time.patch \
           file://tcpslice-CVS.20010207-bpf.patch \
           "
SRC_URI[md5sum] = "e329cbeb7e589f132d92c3447c477190"
SRC_URI[sha256sum] = "4096e8debc898cfaa16b5306f1c42f8d18b19e30e60da8d4deb781c8f684c840"

inherit autotools-brokensep

DEPENDS += "libpcap"

# We do not want to autoreconf.  We must specify srcdir as ".".
# We have to set the ac_cv_* cache variables as well as pass the normal
# cross-compilation options to configure!
#
do_configure () {
	oe_runconf \
            --srcdir="." \
            ac_cv_build=${BUILD_SYS} \
            ac_cv_host=${HOST_SYS} \
            ac_cv_target=${HOST_SYS}
}

do_install () {
	mkdir -p ${D}/usr/sbin
	install -c -m 555 tcpslice ${D}/usr/sbin
}

