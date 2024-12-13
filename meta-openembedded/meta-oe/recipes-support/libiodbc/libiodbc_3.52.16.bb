SUMMARY = "iODBC driver manager maintained by OpenLink Software."

DESCRIPTION = "This kit will provide you with everything you need to \
develop ODBC-compliant applications under Unix without having to pay \
royalties to other parties. \
"

HOMEPAGE = "http://www.iodbc.org/"

LICENSE = "LGPL-2.0-only | BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL;md5=8c0138a8701f0e1282f5c8b2c7d39114 \
                    file://LICENSE.BSD;md5=5b36348a404e1d65b57df8d33fd6e414 \
                    "

SRC_URI = "https://github.com/openlink/iODBC/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "3898b32d07961360f6f2cf36db36036b719a230e476469258a80f32243e845fa"

UPSTREAM_CHECK_URI = "https://github.com/openlink/iODBC/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

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
