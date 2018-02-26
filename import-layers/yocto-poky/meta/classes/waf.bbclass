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

python waf_preconfigure() {
    from distutils.version import StrictVersion
    srcsubdir = d.getVar('S')
    wafbin = os.path.join(srcsubdir, 'waf')
    status, result = oe.utils.getstatusoutput(wafbin + " --version")
    if status != 0:
        bb.warn("Unable to execute waf --version, exit code %d. Assuming waf version without bindir/libdir support." % status)
        return
    version = result.split()[1]
    if StrictVersion(version) >= StrictVersion("1.8.7"):
        d.setVar("WAF_EXTRA_CONF", "--bindir=${bindir} --libdir=${libdir}")
}

do_configure[prefuncs] += "waf_preconfigure"

waf_do_configure() {
	${S}/waf configure --prefix=${prefix} ${WAF_EXTRA_CONF} ${EXTRA_OECONF}
}

waf_do_compile()  {
	${S}/waf build ${@get_waf_parallel_make(d)}
}

waf_do_install() {
	${S}/waf install --destdir=${D}
}

EXPORT_FUNCTIONS do_configure do_compile do_install
