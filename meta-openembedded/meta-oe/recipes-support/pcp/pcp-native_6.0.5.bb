require pcp.inc
inherit python3native native 
#autotools-brokensep 
DEPENDS = "python3-native python3-setuptools-native flex-native bison-native"

export PCP_DIR = "${D}"
export PCP_TMP_DIR = "${D}/tmp"
export PCP_BIN_DIR = "${D}/usr/bin"

B = "${S}"

do_configure:prepend() {
    export SED=${TMPDIR}/hosttools/sed
    export AR=${TMPDIR}/hosttools/ar
#    export PYTHON=python3
    
    rm -rf ${S}/include/pcp/configsz.h
    rm -rf ${S}/include/pcp/platformsz.h

}
do_compile:prepend() {
    sed -i -e "s,#undef HAVE_64BIT_LONG,,g" \
           -e "s,#undef HAVE_64BIT_PTR,,g" \
           -e "s,#undef PM_SIZEOF_SUSECONDS_T,,g" \
           -e "s,#undef PM_SIZEOF_TIME_T,,g" \
           ${S}/src/include/pcp/config.h.in

    export AR=${TMPDIR}/hosttools/ar
#    export PYTHON=python3
}

do_compile() {
        oe_runmake default_pcp
}

do_install () {
    oe_runmake install \
    PCP_ETC_DIR=${D}/${sysconfdir} \
    PCP_SYSCONF_DIR=${D}/${sysconfdir} \
    PCP_VAR_DIR=${D}/${localstatedir} \
    PCP_SHARE_DIR=${D}/${datadir} \
    PCP_BIN_DIR=${D}/${bindir} \
    PCP_BINADM_DIR=${D}/${libexecdir}/pcp/bin \
    PCP_LIBADM_DIR=${D}/${libdir} \
    PCP_LIB_DIR=${D}/${libdir} \
    PCP_MAN_DIR=${D}/${mandir} \
    PCP_DOC_DIR=${D}/${docdir} 
}
