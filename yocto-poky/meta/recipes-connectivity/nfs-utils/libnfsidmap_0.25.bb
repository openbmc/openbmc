SUMMARY = "NFS id mapping library"
HOMEPAGE = "http://www.citi.umich.edu/projects/nfsv4/linux/"
SECTION = "libs"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=d9c6a2a0ca6017fda7cd905ed2739b37"

SRC_URI = "http://www.citi.umich.edu/projects/nfsv4/linux/libnfsidmap/${BPN}-${PV}.tar.gz \
           file://fix-ac-prereq.patch \
           file://Set_nobody_user_group.patch \
           file://0001-include-sys-types.h-for-getting-u_-typedefs.patch \
          "

SRC_URI[md5sum] = "2ac4893c92716add1a1447ae01df77ab"
SRC_URI[sha256sum] = "656d245d84400e1030f8f40a5a27da76370690c4a932baf249110f047fe7efcf"

UPSTREAM_CHECK_URI = "http://www.citi.umich.edu/projects/nfsv4/linux/libnfsidmap/"

inherit autotools

EXTRA_OECONF = "--disable-ldap"

do_install_append () {
	install -d ${D}${sysconfdir}/
	install -m 0644 ${WORKDIR}/${BPN}-${PV}/idmapd.conf ${D}${sysconfdir}/idmapd.conf
}

