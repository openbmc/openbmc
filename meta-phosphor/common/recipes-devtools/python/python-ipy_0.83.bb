SUMMARY = "Python module for handling IPv4 and IPv6 Addresses and Networks"
DESCRIPTION = "IPy is a Python module for handling IPv4 and IPv6 Addresses and Networks \ 
in a fashion similar to perl's Net::IP and friends. The IP class allows \
a comfortable parsing and handling for most notations in use for IPv4 \
and IPv6 Addresses and Networks."
SECTION = "devel/python"
HOMEPAGE = "https://github.com/haypo/python-ipy"
DEPENDS = "python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=ebc0028ff5cdaf7796604875027dcd55"

SRC_URI = "http://pypi.python.org/packages/source/I/IPy/IPy-${PV}.tar.gz"

SRC_URI[md5sum] = "7b8c6eb4111b15aea31b67108e769712"
SRC_URI[sha256sum] = "61da5a532b159b387176f6eabf11946e7458b6df8fb8b91ff1d345ca7a6edab8"

S = "${WORKDIR}/IPy-${PV}"

inherit distutils

# need to export these variables for python-config to work
export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

BBCLASSEXTEND = "native"

do_install_append() {
	install -d ${D}/${datadir}/doc/${BPN}-${PV}
	install AUTHORS COPYING ChangeLog README ${D}/${datadir}/doc/${BPN}-${PV}
}
