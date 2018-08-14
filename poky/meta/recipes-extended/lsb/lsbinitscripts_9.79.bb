SUMMARY = "SysV init scripts which are only used in an LSB image"
HOMEPAGE = "https://wiki.debian.org/LSBInitScripts"
SECTION = "base"
LICENSE = "GPLv2"
DEPENDS = "popt glib-2.0"

RPROVIDES_${PN} += "initd-functions"
RDEPENDS_${PN} += "util-linux"
RCONFLICTS_${PN} = "initscripts-functions"

LIC_FILES_CHKSUM = "file://COPYING;md5=ebf4e8b49780ab187d51bd26aaa022c6"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/fedora-sysv/initscripts \
           file://functions.patch \
           file://0001-functions-avoid-exit-1-which-causes-init-scripts-to-.patch \
          " 
SRCREV = "a51c1b4f7dcf55b568b2ee4c2b18078849943469"
UPSTREAM_CHECK_GITTAGREGEX = "^(?P<pver>\d+(\.\d+)+)"

SRC_URI[md5sum] = "d6c798f40dceb117e12126d94cb25a9a"
SRC_URI[sha256sum] = "1793677bdd1f7ee4cb00878ce43346196374f848a4c8e4559e086040fc7487db"

# Since we are only taking the patched version of functions, no need to
# configure or compile anything so do not execute these
do_configure[noexec] = "1" 
do_compile[noexec] = "1" 

do_install(){
	install -d ${D}${sysconfdir}/init.d/
	install -m 0644 ${S}/rc.d/init.d/functions ${D}${sysconfdir}/init.d/functions
}
