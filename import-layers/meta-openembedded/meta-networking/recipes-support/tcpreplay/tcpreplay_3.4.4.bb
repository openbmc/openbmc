SUMMARY = "Use previously captured traffic to test network devices"

HOMEPAGE = "http://tcpreplay.synfin.net/"
SECTION = "net"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=c33cccf72cc1603e8a72a84811ae3ac8"

SRC_URI = "http://prdownloads.sourceforge.net/tcpreplay/${PV}/tcpreplay-${PV}.tar.gz \
           file://tcpreplay-3.4.4-cross-compile.patch \
           file://tcpreplay-3.4.4-no-bfp-support.patch \
           file://tcpreplay-3.4.4-fix-unable-to-link-libpcap-issue.patch \
           file://tcpreplay-3.4.4-improve-search-for-libpcap.patch \
           "
SRC_URI[md5sum] = "22725feb9b2590809f9350308ec65180"
SRC_URI[sha256sum] = "7a809c58ddec86407fd6e5597ac883d7874a19bea81d716bb2b1c6e3b0e7b58f"

DEPENDS = "libpcap"

EXTRA_OECONF += "--with-libpcap=${STAGING_DIR_HOST}/usr"

inherit siteinfo autotools-brokensep

