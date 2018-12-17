DESCRIPTION = "FastCGI is a protocol for interfacing interactive programs with a web server."
HOMEPAGE = "http://www.fastcgi.com"
LICENSE = "OML"
LIC_FILES_CHKSUM = "file://LICENSE.TERMS;md5=e3aacac3a647af6e7e31f181cda0a06a"

SRCREV = "68100b5b8cb26f04b784778ed19ccef4fe389f57"
PV = "2.4.1+git${SRCPV}"

SRC_URI = "git://github.com/FastCGI-Archives/fcgi2.git;protocol=https \
           file://add_foreign_to_AM_INIT_AUTOMAKE.patch \
"

S = "${WORKDIR}/git"

inherit autotools

PARALLEL_MAKE = ""
