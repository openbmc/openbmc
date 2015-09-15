HOST_DATA ?= "${TMPDIR}/host-contamination-data/"
SWABBER_REPORT ?= "${LOG_DIR}/swabber/"
SWABBER_LOGS ?= "${LOG_DIR}/contamination-logs"
TRACE_LOGDIR ?= "${SWABBER_LOGS}/${PACKAGE_ARCH}"
TRACE_LOGFILE = "${TRACE_LOGDIR}/${PN}-${PV}"

SWAB_ORIG_TASK := "${BB_DEFAULT_TASK}"
BB_DEFAULT_TASK = "generate_swabber_report"

# Several recipes don't build with parallel make when run under strace
# Ideally these should be fixed but as a temporary measure disable parallel
# builds for troublesome recipes
PARALLEL_MAKE_pn-openssl = ""
PARALLEL_MAKE_pn-glibc = ""
PARALLEL_MAKE_pn-glib-2.0 = ""
PARALLEL_MAKE_pn-libxml2 = ""
PARALLEL_MAKE_pn-readline = ""
PARALLEL_MAKE_pn-util-linux = ""
PARALLEL_MAKE_pn-binutils = ""
PARALLEL_MAKE_pn-bison = ""
PARALLEL_MAKE_pn-cmake = ""
PARALLEL_MAKE_pn-elfutils = ""
PARALLEL_MAKE_pn-gcc = ""
PARALLEL_MAKE_pn-gcc-runtime = ""
PARALLEL_MAKE_pn-m4 = ""
PARALLEL_MAKE_pn-opkg = ""
PARALLEL_MAKE_pn-pkgconfig = ""
PARALLEL_MAKE_pn-prelink = ""
PARALLEL_MAKE_pn-rpm = ""
PARALLEL_MAKE_pn-tcl = ""
PARALLEL_MAKE_pn-beecrypt = ""
PARALLEL_MAKE_pn-curl = ""
PARALLEL_MAKE_pn-gmp = ""
PARALLEL_MAKE_pn-libmpc = ""
PARALLEL_MAKE_pn-libxslt = ""
PARALLEL_MAKE_pn-lzo = ""
PARALLEL_MAKE_pn-popt = ""
PARALLEL_MAKE_pn-linux-wrs = ""
PARALLEL_MAKE_pn-libgcrypt = ""
PARALLEL_MAKE_pn-gpgme = ""
PARALLEL_MAKE_pn-udev = ""
PARALLEL_MAKE_pn-gnutls = ""

python() {
    # NOTE: It might be useful to detect host infection on native and cross
    # packages but as it turns out to be pretty hard to do this for all native
    # and cross packages which aren't swabber-native or one of its dependencies
    # I have ignored them for now...
    if not bb.data.inherits_class('native', d) and not bb.data.inherits_class('nativesdk', d) and not bb.data.inherits_class('cross', d):
        deps = (d.getVarFlag('do_setscene', 'depends') or "").split()
        deps.append('strace-native:do_populate_sysroot')
        d.setVarFlag('do_setscene', 'depends', " ".join(deps))
        logdir = d.expand("${TRACE_LOGDIR}")
        bb.utils.mkdirhier(logdir)
    else:
        d.setVar('STRACEFUNC', '')
}

STRACEPID = "${@os.getpid()}"
STRACEFUNC = "imageswab_attachstrace"

do_configure[prefuncs] += "${STRACEFUNC}"
do_compile[prefuncs] += "${STRACEFUNC}"

imageswab_attachstrace () {
	STRACE=`which strace`

	if [ -x "$STRACE" ]; then
		swabber-strace-attach "$STRACE -f -o ${TRACE_LOGFILE}-${BB_CURRENTTASK}.log -e trace=open,execve -p ${STRACEPID}" "${TRACE_LOGFILE}-traceattach-${BB_CURRENTTASK}.log"
	fi
}

do_generate_swabber_report () {

	update_distro ${HOST_DATA}

	# Swabber can't create the directory for us
	mkdir -p ${SWABBER_REPORT}

	REPORTSTAMP=${SWAB_ORIG_TASK}-`date +%2m%2d%2H%2M%Y`

	if [ `which ccache` ] ; then
		CCACHE_DIR=`( ccache -s | grep "cache directory" | grep -o '[^ ]*$' 2> /dev/null )`
 	fi

	if [ "$(ls -A ${HOST_DATA})" ]; then
		echo "Generating swabber report"
		swabber -d ${HOST_DATA} -l ${SWABBER_LOGS} -o ${SWABBER_REPORT}/report-${REPORTSTAMP}.txt -r ${SWABBER_REPORT}/extra_report-${REPORTSTAMP}.txt -c all -p ${TOPDIR} -f ${OEROOT}/meta/conf/swabber ${TOPDIR} ${OEROOT} ${CCACHE_DIR}
	else
		echo "No host data, cannot generate swabber report."
	fi
}
addtask generate_swabber_report after do_${SWAB_ORIG_TASK}
do_generate_swabber_report[depends] = "swabber-native:do_populate_sysroot"
