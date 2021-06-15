# avoids build breaks when using no-static-libs.inc
DISABLE_STATIC = ""

# What Python interpretter to use.  Defaults to Python 3 but can be
# overridden if required.
WAF_PYTHON ?= "python3"

B = "${WORKDIR}/build"
do_configure[cleandirs] += "${B}"

EXTRA_OECONF_append = " ${PACKAGECONFIG_CONFARGS}"

EXTRA_OEWAF_BUILD ??= ""
# In most cases, you want to pass the same arguments to `waf build` and `waf
# install`, but you can override it if necessary
EXTRA_OEWAF_INSTALL ??= "${EXTRA_OEWAF_BUILD}"

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
    python = d.getVar('WAF_PYTHON')
    wafbin = os.path.join(subsrcdir, 'waf')
    try:
        result = subprocess.check_output([python, wafbin, '--version'], cwd=subsrcdir, stderr=subprocess.STDOUT)
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
	(cd ${S} && ${WAF_PYTHON} ./waf configure -o ${B} --prefix=${prefix} ${WAF_EXTRA_CONF} ${EXTRA_OECONF})
}

do_compile[progress] = "outof:^\[\s*(\d+)/\s*(\d+)\]\s+"
waf_do_compile()  {
	(cd ${S} && ${WAF_PYTHON} ./waf build ${@oe.utils.parallel_make_argument(d, '-j%d', limit=64)} ${EXTRA_OEWAF_BUILD})
}

waf_do_install() {
	(cd ${S} && ${WAF_PYTHON} ./waf install --destdir=${D} ${EXTRA_OEWAF_INSTALL})
}

EXPORT_FUNCTIONS do_configure do_compile do_install
