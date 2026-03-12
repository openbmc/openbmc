DESCRIPTION = "FastCGI is a protocol for interfacing interactive programs with a web server."
HOMEPAGE = "http://www.fastcgi.com"
LICENSE = "OML"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3aacac3a647af6e7e31f181cda0a06a"

SRCREV = "47f2c03b7771f0ef61d887734ef91e6fa747f837"

SRC_URI = "git://github.com/FastCGI-Archives/fcgi2.git;protocol=https;branch=master;tag=${PV} \
          "


inherit autotools

PARALLEL_MAKE = ""
