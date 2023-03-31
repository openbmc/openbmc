SUMMARY = "iODBC driver manager maintained by OpenLink Software."

DESCRIPTION = "This kit will provide you with everything you need to \
develop ODBC-compliant applications under Unix without having to pay \
royalties to other parties. \
"

HOMEPAGE = "http://www.iodbc.org/"

LICENSE = "LGPL-2.0-only | BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL;md5=8c0138a8701f0e1282f5c8b2c7d39114 \
                    file://LICENSE.BSD;md5=ff3a66a194e500df485da930da7f2c62 \
                    "

SRC_URI = "https://github.com/openlink/iODBC/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "c74541e3327fc9a1c7ccf103645471c67bc014542d70f572476eb07c0b2dd43c"

inherit autotools

EXTRA_OECONF += " --prefix=/usr/local \
		--includedir=/usr/include/iodbc \
		--with-iodbc-inidir=/etc \
		--enable-odbc3 \
		--enable-pthreads \
		--disable-libodbc \
		--disable-static \
		"

inherit multilib_script
MULTILIB_SCRIPTS = "${PN}:${bindir}/iodbc-config"
