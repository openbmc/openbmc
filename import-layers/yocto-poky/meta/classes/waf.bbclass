# avoids build breaks when using no-static-libs.inc
DISABLE_STATIC = ""

EXTRA_OECONF_append = " ${PACKAGECONFIG_CONFARGS}"

def get_waf_parallel_make(d):
    pm = d.getVar('PARALLEL_MAKE')
    if pm:
        # look for '-j' and throw other options (e.g. '-l') away
        # because they might have different meaning in bjam
        pm = pm.split()
        while pm:
            v = None
            opt = pm.pop(0)
            if opt == '-j':
                v = pm.pop(0)
            elif opt.startswith('-j'):
                v = opt[2:].strip()
            else:
                v = None

            if v:
                v = min(64, int(v))
                return '-j' + str(v)

    return ""

waf_do_configure() {
	${S}/waf configure --prefix=${prefix} ${EXTRA_OECONF}
}

waf_do_compile()  {
	${S}/waf build ${@get_waf_parallel_make(d)}
}

waf_do_install() {
	${S}/waf install --destdir=${D}
}

EXPORT_FUNCTIONS do_configure do_compile do_install
