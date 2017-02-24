SUMMARY = "Free peer-reviewed portable C++ source libraries"
SECTION = "libs"
DEPENDS = "bjam-native zlib bzip2"

ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"

BOOST_LIBS = "\
	atomic \
	chrono \
	container \
	date_time \
	exception \
	filesystem \
	graph \
	iostreams \
	log \
	math \
	program_options \
	random \
	regex \
	serialization \
	signals \
	system \
	timer \
	test \
	thread \
	wave \
	"

# only supported by x86 and powerpc
BOOST_LIBS_append_x86 = " context coroutine"
BOOST_LIBS_append_x86-64 = " context coroutine"
BOOST_LIBS_append_powerpc = " context coroutine"
# need consistent settings for native builds (x86 override not applied for native)
BOOST_LIBS_remove_class-native = " context coroutine"

# optional libraries
PACKAGECONFIG ??= "locale"
PACKAGECONFIG[locale] = ",,icu"
PACKAGECONFIG[graph_parallel] = ",,,boost-mpi mpich"
PACKAGECONFIG[mpi] = ",,mpich"
PACKAGECONFIG[python] = ",,python python3"

BOOST_LIBS += "\
    ${@bb.utils.contains('PACKAGECONFIG', 'locale', 'locale', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'graph_parallel', 'graph_parallel mpi', \
                         bb.utils.contains('PACKAGECONFIG', 'mpi', 'mpi', '', d), d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'python', 'python python3', '', d)} \
"

inherit python-dir
PYTHON_ROOT = "${STAGING_DIR_HOST}/${prefix}"

# Make a package for each library, plus -dev
PACKAGES = "${PN}-dbg ${BOOST_PACKAGES}"
python __anonymous () {
    packages = []
    extras = []
    for lib in d.getVar('BOOST_LIBS', True).split( ):
        # BJAM does not know '--with-python3' (only --with-python)
        if lib != "python3":
            extras.append("--with-%s" % lib)
        pkg = "boost-%s" % lib.replace("_", "-")
        packages.append(pkg)
        if lib == "python":
            # special: python*.so matches python3.so !!
            if not d.getVar("FILES_%s" % pkg, True):
                    d.setVar("FILES_%s" % pkg, "${libdir}/libboost_%s.so.*" % lib)
        else:
            if not d.getVar("FILES_%s" % pkg, True):
                    d.setVar("FILES_%s" % pkg, "${libdir}/libboost_%s*.so.*" % lib)
    d.setVar("BOOST_PACKAGES", " ".join(packages))
    d.setVar("BJAM_EXTRA", " ".join(extras))
}

# Override the contents of specific packages
FILES_${PN}-graph = "${libdir}/libboost_graph.so.*"
FILES_${PN}-graph_parallel = "${libdir}/libboost_graph_parallel.so.*"
FILES_${PN}-locale = "${libdir}/libboost_locale.so.*"
FILES_${PN}-mpi = "${libdir}/mpi.so ${libdir}/libboost_mpi*.so.*"
FILES_boost-serialization = "${libdir}/libboost_serialization*.so.* \
	${libdir}/libboost_wserialization*.so.*"
FILES_boost-test = "${libdir}/libboost_prg_exec_monitor*.so.* \
	${libdir}/libboost_unit_test_framework*.so.*"

# -dev last to pick up the remaining stuff
PACKAGES += "${PN}-dev ${PN}-staticdev"
FILES_${PN} = ""
FILES_${PN}-dev = "${includedir} ${libdir}/libboost_*.so"
FILES_${PN}-staticdev = "${libdir}/libboost_*.a"

# "boost" is a metapackage which pulls in all boost librabries
PACKAGES += "${PN}"
RRECOMMENDS_${PN} += "${BOOST_PACKAGES}"
RRECOMMENDS_${PN}_class-native = ""
ALLOW_EMPTY_${PN} = "1"

# to avoid GNU_HASH QA errors added LDFLAGS to ARCH; a little bit dirty but at least it works
TARGET_CC_ARCH += "${LDFLAGS}"

# Oh yippee, a new build system, it's sooo cooool I could eat my own
# foot.  inlining=on lets the compiler choose, I think.  At least this
# stuff is documented...
# NOTE: if you leave <debug-symbols>on then in a debug build the build sys
# objcopy will be invoked, and that won't work.  Building debug apparently
# requires hacking gcc-tools.jam
#
# Sometimes I wake up screaming.  Famous figures are gathered in the nightmare,
# Steve Bourne, Larry Wall, the whole of the ANSI C committee.  They're just
# standing there, waiting, but the truely terrifying thing is what they carry
# in their hands.  At first sight each seems to bear the same thing, but it is
# not so for the forms in their grasp are ever so slightly different one from
# the other.  Each is twisted in some grotesque way from the other to make each
# an unspeakable perversion impossible to perceive without the onset of madness.
# True insanity awaits anyone who perceives all of these horrors together.
#
# Quotation marks, there might be an easier way to do this, but I can't find
# it.  The problem is that the user.hpp configuration file must receive a
# pre-processor macro defined as the appropriate string - complete with "'s
# around it.  (<> is a possibility here but the danger to that is that the
# failure case interprets the < and > as shell redirections, creating
# random files in the source tree.)
#
#bjam: '-DBOOST_PLATFORM_CONFIG=\"config\"'
#do_compile: '-sGCC=... '"'-DBOOST_PLATFORM_CONFIG=\"config\"'"
SQD = '"'
EQD = '\"'
#boost.bb:   "...  '-sGCC=... '${SQD}'-DBOOST_PLATFORM_CONFIG=${EQD}config${EQD}'${SQD} ..."
BJAM_CONF = "${SQD}'-DBOOST_PLATFORM_CONFIG=${EQD}boost/config/platform/${TARGET_OS}.hpp${EQD}'${SQD}"

BJAM_TOOLS   = "--ignore-site-config \
		'-sTOOLS=gcc' \
		'-sGCC=${CC} '${BJAM_CONF} \
		'-sGXX=${CXX} '${BJAM_CONF} \
		'-sGCC_INCLUDE_DIRECTORY=${STAGING_INCDIR}' \
		'-sGCC_STDLIB_DIRECTORY=${STAGING_LIBDIR}' \
		'-sBUILD=release <optimization>space <threading>multi <inlining>on <debug-symbols>off' \
		'-sPYTHON_ROOT=${PYTHON_ROOT}' \
		'--layout=system' \
		"

# use PARALLEL_MAKE to speed up the build, but limit it by -j 64, greater parallelism causes bjam to segfault or to ignore -j
# https://svn.boost.org/trac/boost/ticket/7634
def get_boost_parallel_make(d):
    pm = d.getVar('PARALLEL_MAKE', True)
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

BOOST_PARALLEL_MAKE = "${@get_boost_parallel_make(d)}"
BJAM_OPTS    = '${BOOST_PARALLEL_MAKE} -d+2 -q \
		${BJAM_TOOLS} \
		-sBOOST_BUILD_USER_CONFIG=${WORKDIR}/user-config.jam \
		--build-dir=${S}/${TARGET_SYS} \
		--disable-icu \
		${BJAM_EXTRA}'

# Native compilation of bzip2 isn't working
BJAM_OPTS_append_class-native = ' -sNO_BZIP2=1'

do_configure() {
	cp -f ${S}/boost/config/platform/linux.hpp ${S}/boost/config/platform/linux-gnueabi.hpp

	# D2194:Fixing the failure of "error: duplicate initialization of gcc with the following parameters" during compilation.
	rm -f ${WORKDIR}/user-config.jam
	echo 'using gcc : 4.3.1 : ${CXX} : <cflags>"${CFLAGS}" <cxxflags>"${CXXFLAGS}" <linkflags>"${LDFLAGS}" ;' >> ${WORKDIR}/user-config.jam
	echo "using python : ${PYTHON_BASEVERSION} : : ${STAGING_INCDIR}/python${PYTHON_BASEVERSION} ;" >> ${WORKDIR}/user-config.jam
	echo "using python : 3.5 : : ${STAGING_INCDIR}/python3.5m ;" >> ${WORKDIR}/user-config.jam

	if ${@bb.utils.contains('BOOST_LIBS', 'mpi', 'true', 'false', d)}; then
		echo "using mpi : : <find-shared-library>mpi ;" >> ${WORKDIR}/user-config.jam
	fi

	CC="${BUILD_CC}" CFLAGS="${BUILD_CFLAGS}" ./bootstrap.sh --with-bjam=bjam --with-toolset=gcc --with-python-root=${PYTHON_ROOT}
	sed -i '/^using python/d' ${S}/project-config.jam
}

do_compile() {
	set -ex
	rm -rf ${S}/${TARGET_SYS}
	bjam ${BJAM_OPTS} --prefix=${prefix} \
		--exec-prefix=${exec_prefix} \
		--libdir=${libdir} \
		--includedir=${includedir}
}

do_install() {
	set -ex
	bjam ${BJAM_OPTS} \
		--libdir=${D}${libdir} \
		--includedir=${D}${includedir} \
		install
	for lib in ${BOOST_LIBS}; do
		if [ -e ${D}${libdir}/libboost_${lib}.a ]; then
			ln -s libboost_${lib}.a ${D}${libdir}/libboost_${lib}-mt.a
		fi
		if [ -e ${D}${libdir}/libboost_${lib}.so ]; then
			ln -s libboost_${lib}.so ${D}${libdir}/libboost_${lib}-mt.so
		fi
	done

}

BBCLASSEXTEND = "native nativesdk"
