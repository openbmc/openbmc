# avoids build breaks when using no-static-libs.inc
DISABLE_STATIC = ""

B = "${WORKDIR}/build"

EXTRA_OECONF_append = " ${PACKAGECONFIG_CONFARGS}"

def waflock_hash(d):
    # Calculates the hash used for the waf lock file. This should include
    # all of the user controllable inputs passed to waf configure. Note
    # that the full paths for ${B} and ${S} are used; this is OK and desired
    # because a change to either of these should create a unique lock file
    # to prevent collisions.
    import hashlib
    h = hashlib.sha512()
    def update(name):
        val = d.getVar(name)
        if val is not None:
            h.update(val.encode('utf-8'))
    update('S')
    update('B')
    update('prefix')
    update('EXTRA_OECONF')
    return h.hexdigest()

# Use WAFLOCK to specify a separate lock file. The build is already
# sufficiently isolated by setting the output directory, this ensures that
# bitbake won't step on toes of any other configured context in the source
# directory (e.g. if the source is coming from externalsrc and was previously
# configured elsewhere).
export WAFLOCK = ".lock-waf_oe_${@waflock_hash(d)}_build"
BB_HASHBASE_WHITELIST += "WAFLOCK"

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
	(cd ${S} && ./waf configure -o ${B} --prefix=${prefix} ${WAF_EXTRA_CONF} ${EXTRA_OECONF})
}

do_compile[progress] = "outof:^\[\s*(\d+)/\s*(\d+)\]\s+"
waf_do_compile()  {
	(cd ${S} && ./waf build ${@oe.utils.parallel_make_argument(d, '-j%d', limit=64)})
}

waf_do_install() {
	(cd ${S} && ./waf install --destdir=${D})
}

EXPORT_FUNCTIONS do_configure do_compile do_install
