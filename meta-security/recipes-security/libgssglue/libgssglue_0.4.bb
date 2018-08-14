SUMMARY = "Exports a gssapi interface which calls other gssapi libraries"
DESCRIPTION = "\
This library exports a gssapi interface, but does not implement any gssapi \
mechanisms itself; instead it calls gssapi routines in other libraries, \
depending on the mechanism. \
"

HOMEPAGE = "http://www.citi.umich.edu/projects/nfsv4/linux/"
SECTION = "libs"

LICENSE = "BSD-3-Clause | HPND"

#Copyright (c) 1996, by Sun Microsystems, Inc.                   HPND
#Copyright (c) 2007 The Regents of the University of Michigan. BSD-3-Clause
#Copyright 1995 by the Massachusetts Institute of Technology.  HPND without Disclaimer
#Copyright 1993 by OpenVision Technologies, Inc.               HPND
LIC_FILES_CHKSUM = "file://COPYING;md5=56871e72a5c475289c0d5e4ba3f2ee3a \
                    file://src/g_accept_sec_context.c;beginline=3;endline=23;md5=8a7f4017cb7f4be49f8981cb8c472690 \
                    file://src/g_ccache_name.c;beginline=1;endline=32;md5=208d4de05d5c8273963a8332f084faa7 \
                    file://src/oid_ops.c;beginline=1;endline=26;md5=1f194d148b396972da26759a8ec399f0 \
                    file://src/oid_ops.c;beginline=378;endline=398;md5=e02c165cb8383e950214baca2fbd664b \
"

SRC_URI = "http://www.citi.umich.edu/projects/nfsv4/linux/${BPN}/${BP}.tar.gz \
           file://libgssglue-canon-name.patch  \
           file://libgssglue-gss-inq-cred.patch  \
           file://libgssglue-mglueP.patch \
           file://libgssglue-g-initialize.patch \
           file://libgssglue-fix-CVE-2011-2709.patch \
"

SRC_URI[md5sum] = "088797f3180702fa54e786496b32e750"
SRC_URI[sha256sum] = "3f791a75502ba723e5e85e41e5e0c711bb89e2716b7c0ec6e74bd1df6739043a"

# gssglue can use krb5, spkm3... as gssapi library, configurable
RRECOMMENDS_${PN} += "krb5"

inherit autotools

do_install_append() {
    # install some docs
    install -d -m 0755 ${D}${docdir}/${BPN}
    install -m 0644 ${S}/AUTHORS ${S}/ChangeLog ${S}/NEWS ${S}/README ${D}${docdir}/${BPN}

    # install the gssapi_mech.conf
    install -d -m 0755 ${D}${sysconfdir}
    install -m 0644 ${S}/doc/gssapi_mech.conf ${D}${sysconfdir}
    
    # change the libgssapi_krb5.so path and name(it is .so.2)
    sed -i -e "s:/usr/lib/libgssapi_krb5.so:libgssapi_krb5.so.2:" ${D}${sysconfdir}/gssapi_mech.conf
}
