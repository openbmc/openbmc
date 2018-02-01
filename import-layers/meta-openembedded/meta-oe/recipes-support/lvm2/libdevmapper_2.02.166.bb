require lvm2.inc

SRC_URI[md5sum] = "c5a54ee0b86703daaad6e856439e115a"
SRC_URI[sha256sum] = "e120b066b85b224552efda40204488c5123de068725676fd6e5c8bc655051b94"

DEPENDS += "autoconf-archive-native"

PACKAGECONFIG = ""

# Unset user/group to unbreak install.
EXTRA_OECONF = "--with-user= \
                --with-group= \
                --enable-pkgconfig \
                --with-usrlibdir=${libdir} \
"

TARGET_CC_ARCH += "${LDFLAGS}"

do_install_append() {
    # Remove things unrelated to libdevmapper
    rm -rf ${D}${sysconfdir}
    for i in `ls ${D}${sbindir}/*`; do
	if [ $i != ${D}${sbindir}/dmsetup ]; then
	    rm $i
	fi
    done
    # Remove docs
    rm -rf ${D}${datadir}
}

RRECOMMENDS_${PN} += "lvm2-udevrules"
