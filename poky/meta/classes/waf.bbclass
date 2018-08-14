# avoids build breaks when using no-static-libs.inc
DISABLE_STATIC = ""

EXTRA_OECONF_append = " ${PACKAGECONFIG_CONFARGS}"

python waf_preconfigure() {
    import subprocess
    from distutils.version import StrictVersion
    subsrcdir = d.getVar('S')
    wafbin = os.path.join(subsrcdir, 'waf')
    try:
        result = subprocess.check_output([wafbin, '--version'], cwd=subsrcdir, stderr=subprocess.STDOUT)
        version = result.decode('utf-8').split()[1]
        if StrictVersion(version) >= StrictVersion("1.8.7"):
            d.setVar("WAF_EXTRA_CONF", "--bindir=${bindir} --libdir=${libdir}")
    except subprocess.CalledProcessError as e:
        bb.warn("Unable to execute waf --version, exit code %d. Assuming waf version without bindir/libdir support." % e.returncode)
    except FileNotFoundError:
        bb.fatal("waf does not exist in %s" % subsrcdir)
}

do_configure[prefuncs] += "waf_preconfigure"

waf_do_configure() {
	${S}/waf configure --prefix=${prefix} ${WAF_EXTRA_CONF} ${EXTRA_OECONF}
}

do_compile[progress] = "outof:^\[\s*(\d+)/\s*(\d+)\]\s+"
waf_do_compile()  {
	${S}/waf build ${@oe.utils.parallel_make_argument(d, '-j%d', limit=64)}
}

waf_do_install() {
	${S}/waf install --destdir=${D}
}

EXPORT_FUNCTIONS do_configure do_compile do_install
