SUMMARY = "cdparanoia library"
DESCRIPTION = "library used to read audio CDs, which is able to perform \
error corrections, hence the name paranoia."
HOMEPAGE = "https://www.xiph.org/"
SECTION = "multimedia"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING-LGPL;md5=d370feaa1c9edcdbd29ca27ea3d2304d"

SRC_URI = "http://downloads.xiph.org/releases/cdparanoia/cdparanoia-III-${PV}.src.tgz \
           file://0001-Use-DESTDIR-in-install-Makefile-rule.patch \
           file://0002-interface-remove-C-reserved-keyword.patch \
"
SRC_URI[md5sum] = "b304bbe8ab63373924a744eac9ebc652"
SRC_URI[sha256sum] = "005db45ef4ee017f5c32ec124f913a0546e77014266c6a1c50df902a55fe64df"

# Uppercase letters are not allowed in the recipe name, thus the recipe can not be named cdparanoia-III and
# we need to add the path to the extracted sources explicitely:
S = "${WORKDIR}/cdparanoia-III-${PV}"

# cdparanoia Makefile can not be used with several threads (because the static library target and the shared
# library target use object files which are compiled in the same directory, the object files are just removed
# between the compilation of those two targets)
PARALLEL_MAKE = ""

inherit autotools-brokensep
