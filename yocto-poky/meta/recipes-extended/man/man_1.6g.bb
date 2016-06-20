SUMMARY = "Online documentation tools"
DESCRIPTION = "A set of documentation tools: man, apropos and whatis"
SECTION = "console/utils"
HOMEPAGE = "http://primates.ximian.com/~flucifredi/man"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

PR = "r1"

DEPENDS = "groff less"

def compress_pkg(d):
    if "compress_doc" in (d.getVar("INHERIT", True) or "").split():
         compress = d.getVar("DOC_COMPRESS", True)
         if compress == "gz":
             return "gzip"
         elif compress == "bz2":
             return "bzip2"
         elif compress == "xz":
             return "xz"
    return ""

RDEPENDS_${PN} += "${@compress_pkg(d)}"

SRC_URI = "http://pkgs.fedoraproject.org/lookaside/pkgs/man2html/${BP}.tar.gz/ba154d5796928b841c9c69f0ae376660/${BP}.tar.gz \
           file://man-1.5k-confpath.patch;striplevel=0 \
           file://man-1.5h1-make.patch \
           file://man-1.5k-nonascii.patch \
           file://man-1.6e-security.patch \
           file://man-1.6e-mandirs.patch \
           file://man-1.5m2-bug11621.patch \
           file://man-1.5k-sofix.patch \
           file://man-1.5m2-buildroot.patch \
           file://man-1.6e-ro_usr.patch \
           file://man-1.5i2-newline.patch;striplevel=0 \
           file://man-1.5j-utf8.patch \
           file://man-1.5i2-overflow.patch \
           file://man-1.5j-nocache.patch \
           file://man-1.5i2-initial.patch \
           file://man-1.5h1-gencat.patch;striplevel=0 \
           file://man-1.5g-nonrootbuild.patch \
           file://man-1.5j-i18n.patch \
           file://man-1.6e-whatis2.patch \
           file://man-1.6e-use_i18n_vars_in_a_std_way.patch \
           file://man-1.5m2-no-color-for-printing.patch \
           file://man-1.5m2-sigpipe.patch \
           file://man-1.6e-i18n_whatis.patch \
           file://man-1.6e-new_sections.patch \
           file://man.1.gz;unpack=false \
           file://man.7.gz;unpack=false \
           file://man.conf \
           file://manpath.5.gz;unpack=false \
           file://man-1.6g-whatis3.patch \
           file://configure_sed.patch \
           file://man-1.6g-parallel.patch \
           file://man-1.6g-compile-warnings.patch \
           file://man-1.6g-configure.patch \
"

SRC_URI[md5sum] = "ba154d5796928b841c9c69f0ae376660"
SRC_URI[sha256sum] = "ccdcb8c3f4e0080923d7e818f0e4a202db26c46415eaef361387c20995b8959f"

CFLAGS += "-DSYSV"

do_configure () {
        ${S}/configure -default -confdir /etc +sgid +fhs +lang all
}


do_install() {
        oe_runmake install DESTDIR=${D}
}

do_install_append(){
	mkdir -p  ${D}${sysconfdir}
        mkdir -p ${D}${datadir}/man/man5
        mkdir -p ${D}${datadir}/man/man7
	cp ${WORKDIR}/man.conf ${D}${sysconfdir}/man.config
        cp ${WORKDIR}/man.1.gz ${D}${datadir}/man/man1/
        cp ${WORKDIR}/man.7.gz ${D}${datadir}/man/man7/
        cp ${WORKDIR}/manpath.5.gz ${D}${datadir}/man/man5/
}


RDEPENDS_${PN} = "less groff"
FILES_${PN} += "${datadir}/locale ${sysconfdir}/man.config"
