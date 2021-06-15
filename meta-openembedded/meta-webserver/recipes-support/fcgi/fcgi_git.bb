DESCRIPTION = "FastCGI is a protocol for interfacing interactive programs with a web server."
HOMEPAGE = "http://www.fastcgi.com"
LICENSE = "OML"
LIC_FILES_CHKSUM = "file://LICENSE.TERMS;md5=e3aacac3a647af6e7e31f181cda0a06a"

SRCREV = "382aa2b0d53a87c27f2f647dfaf670375ba0b85f"
PV = "2.4.2"

SRC_URI = "git://github.com/FastCGI-Archives/fcgi2.git;protocol=https \
          "

S = "${WORKDIR}/git"

inherit autotools

PARALLEL_MAKE = ""
