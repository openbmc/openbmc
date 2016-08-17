SUMMARY = "SysV init scripts which are only used in an LSB image"
SECTION = "base"
LICENSE = "GPLv2"
DEPENDS = "popt glib-2.0"

RDEPENDS_${PN} += "util-linux"

LIC_FILES_CHKSUM = "file://COPYING;md5=ebf4e8b49780ab187d51bd26aaa022c6"

S="${WORKDIR}/initscripts-${PV}"
SRC_URI = "http://pkgs.fedoraproject.org/repo/pkgs/initscripts/initscripts-${PV}.tar.bz2/9cce2ae1009750e84be37c09a028757e/initscripts-${PV}.tar.bz2 \
           file://functions.patch \
           file://0001-functions-avoid-exit-1-which-causes-init-scripts-to-.patch \
          " 

SRC_URI[md5sum] = "9cce2ae1009750e84be37c09a028757e"
SRC_URI[sha256sum] = "48b59ce8157cfc58bbd4b1dfa58ad1087245761ae11c2033b66ae3864ea7e1cf"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "functions"
ALTERNATIVE_LINK_NAME[functions] = "${sysconfdir}/init.d/functions"

# Since we are only taking the patched version of functions, no need to
# configure or compile anything so do not execute these
do_configure[noexec] = "1" 
do_compile[noexec] = "1" 

do_install(){
	install -d ${D}${sysconfdir}/init.d/
	install -m 0644 ${S}/rc.d/init.d/functions ${D}${sysconfdir}/init.d/functions
}
