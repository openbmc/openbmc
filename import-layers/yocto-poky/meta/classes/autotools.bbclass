def autotools_dep_prepend(d):
    if d.getVar('INHIBIT_AUTOTOOLS_DEPS', True):
        return ''

    pn = d.getVar('PN', True)
    deps = ''

    if pn in ['autoconf-native', 'automake-native', 'help2man-native']:
        return deps
    deps += 'autoconf-native automake-native '

    if not pn in ['libtool', 'libtool-native'] and not pn.endswith("libtool-cross"):
        deps += 'libtool-native '
        if not bb.data.inherits_class('native', d) \
                        and not bb.data.inherits_class('nativesdk', d) \
                        and not bb.data.inherits_class('cross', d) \
                        and not d.getVar('INHIBIT_DEFAULT_DEPS', True):
            deps += 'libtool-cross '

    return deps + 'gnu-config-native '

DEPENDS_prepend = "${@autotools_dep_prepend(d)} "

inherit siteinfo

# Space separated list of shell scripts with variables defined to supply test
# results for autoconf tests we cannot run at build time.
export CONFIG_SITE = "${@siteinfo_get_files(d)}"

acpaths = "default"
EXTRA_AUTORECONF = "--exclude=autopoint"

export lt_cv_sys_lib_dlsearch_path_spec = "${libdir} ${base_libdir}"

# When building tools for use at build-time it's recommended for the build
# system to use these variables when cross-compiling.
# (http://sources.redhat.com/autobook/autobook/autobook_270.html)
export CPP_FOR_BUILD = "${BUILD_CPP}"
export CPPFLAGS_FOR_BUILD = "${BUILD_CPPFLAGS}"

export CC_FOR_BUILD = "${BUILD_CC}"
export CFLAGS_FOR_BUILD = "${BUILD_CFLAGS}"

export CXX_FOR_BUILD = "${BUILD_CXX}"
export CXXFLAGS_FOR_BUILD="${BUILD_CXXFLAGS}"

export LD_FOR_BUILD = "${BUILD_LD}"
export LDFLAGS_FOR_BUILD = "${BUILD_LDFLAGS}"

def append_libtool_sysroot(d):
    # Only supply libtool sysroot option for non-native packages
    if not bb.data.inherits_class('native', d):
        return '--with-libtool-sysroot=${STAGING_DIR_HOST}'
    return ""

CONFIGUREOPTS = " --build=${BUILD_SYS} \
		  --host=${HOST_SYS} \
		  --target=${TARGET_SYS} \
		  --prefix=${prefix} \
		  --exec_prefix=${exec_prefix} \
		  --bindir=${bindir} \
		  --sbindir=${sbindir} \
		  --libexecdir=${libexecdir} \
		  --datadir=${datadir} \
		  --sysconfdir=${sysconfdir} \
		  --sharedstatedir=${sharedstatedir} \
		  --localstatedir=${localstatedir} \
		  --libdir=${libdir} \
		  --includedir=${includedir} \
		  --oldincludedir=${oldincludedir} \
		  --infodir=${infodir} \
		  --mandir=${mandir} \
		  --disable-silent-rules \
		  ${CONFIGUREOPT_DEPTRACK} \
		  ${@append_libtool_sysroot(d)}"
CONFIGUREOPT_DEPTRACK ?= "--disable-dependency-tracking"

CACHED_CONFIGUREVARS ?= ""

AUTOTOOLS_SCRIPT_PATH ?= "${S}"
CONFIGURE_SCRIPT ?= "${AUTOTOOLS_SCRIPT_PATH}/configure"

AUTOTOOLS_AUXDIR ?= "${AUTOTOOLS_SCRIPT_PATH}"

oe_runconf () {
	# Use relative path to avoid buildpaths in files
	cfgscript_name="`basename ${CONFIGURE_SCRIPT}`"
	cfgscript=`python3 -c "import os; print(os.path.relpath(os.path.dirname('${CONFIGURE_SCRIPT}'), '.'))"`/$cfgscript_name
	if [ -x "$cfgscript" ] ; then
		bbnote "Running $cfgscript ${CONFIGUREOPTS} ${EXTRA_OECONF} $@"
		if ! ${CACHED_CONFIGUREVARS} $cfgscript ${CONFIGUREOPTS} ${EXTRA_OECONF} "$@"; then
			bbnote "The following config.log files may provide further information."
			bbnote `find ${B} -ignore_readdir_race -type f -name config.log`
			bbfatal_log "configure failed"
		fi
	else
		bbfatal "no configure script found at $cfgscript"
	fi
}

CONFIGURESTAMPFILE = "${WORKDIR}/configure.sstate"

autotools_preconfigure() {
	if [ -n "${CONFIGURESTAMPFILE}" -a -e "${CONFIGURESTAMPFILE}" ]; then
		if [ "`cat ${CONFIGURESTAMPFILE}`" != "${BB_TASKHASH}" ]; then
			if [ "${S}" != "${B}" ]; then
				echo "Previously configured separate build directory detected, cleaning ${B}"
				rm -rf ${B}
				mkdir -p ${B}
			else
				# At least remove the .la files since automake won't automatically
				# regenerate them even if CFLAGS/LDFLAGS are different
				cd ${S}
				if [ "${CLEANBROKEN}" != "1" -a \( -e Makefile -o -e makefile -o -e GNUmakefile \) ]; then
					oe_runmake clean
				fi
				find ${S} -ignore_readdir_race -name \*.la -delete
			fi
		fi
	fi
}

autotools_postconfigure(){
	if [ -n "${CONFIGURESTAMPFILE}" ]; then
		mkdir -p `dirname ${CONFIGURESTAMPFILE}`
		echo ${BB_TASKHASH} > ${CONFIGURESTAMPFILE}
	fi
}

EXTRACONFFUNCS ??= ""

EXTRA_OECONF_append = " ${PACKAGECONFIG_CONFARGS}"

do_configure[prefuncs] += "autotools_preconfigure autotools_copy_aclocals ${EXTRACONFFUNCS}"
do_configure[postfuncs] += "autotools_postconfigure"

ACLOCALDIR = "${WORKDIR}/aclocal-copy"

python autotools_copy_aclocals () {
    import copy

    s = d.getVar("AUTOTOOLS_SCRIPT_PATH", True)
    if not os.path.exists(s + "/configure.in") and not os.path.exists(s + "/configure.ac"):
        if not d.getVar("AUTOTOOLS_COPYACLOCAL", False):
            return

    taskdepdata = d.getVar("BB_TASKDEPDATA", False)
    #bb.warn(str(taskdepdata))
    pn = d.getVar("PN", True)
    aclocaldir = d.getVar("ACLOCALDIR", True)
    oe.path.remove(aclocaldir)
    bb.utils.mkdirhier(aclocaldir)
    start = None
    configuredeps = []
    # Detect bitbake -b usage
    # Everything but quilt-native would have dependencies
    nodeps = (pn != "quilt-native")

    for dep in taskdepdata:
        data = taskdepdata[dep]
        if data[1] == "do_configure" and data[0] == pn:
            start = dep
        if not nodeps and start:
            break
        if nodeps and data[0] != pn:
            nodeps = False
    if start is None:
        bb.fatal("Couldn't find ourself in BB_TASKDEPDATA?")

    # We need to figure out which m4 files we need to expose to this do_configure task.
    # This needs to match what would get restored from sstate, which is controlled 
    # ultimately by calls from bitbake to setscene_depvalid().
    # That function expects a setscene dependency tree. We build a dependency tree 
    # condensed to do_populate_sysroot -> do_populate_sysroot dependencies, similar to 
    # that used by setscene tasks. We can then call into setscene_depvalid() and decide
    # which dependencies we can "see" and should expose the m4 files for.
    setscenedeps = copy.deepcopy(taskdepdata)

    start = set([start])

    # Create collapsed do_populate_sysroot -> do_populate_sysroot tree
    for dep in taskdepdata:
        data = setscenedeps[dep]        
        if data[1] != "do_populate_sysroot":
            for dep2 in setscenedeps:
                data2 = setscenedeps[dep2]
                if dep in data2[3]:
                    data2[3].update(setscenedeps[dep][3])
                    data2[3].remove(dep)
            if dep in start:
                start.update(setscenedeps[dep][3])
                start.remove(dep)
            del setscenedeps[dep]

    # Remove circular references
    for dep in setscenedeps:
        if dep in setscenedeps[dep][3]:
            setscenedeps[dep][3].remove(dep)

    # Direct dependencies should be present and can be depended upon
    for dep in start:
        configuredeps.append(setscenedeps[dep][0])

    # Call into setscene_depvalid for each sub-dependency and only copy m4 files
    # for ones that would be restored from sstate.
    done = list(start)
    next = list(start)
    while next:
        new = []
        for dep in next:
            data = setscenedeps[dep]
            for datadep in data[3]:
                if datadep in done:
                    continue
                taskdeps = {}
                taskdeps[dep] = setscenedeps[dep][:2]
                taskdeps[datadep] = setscenedeps[datadep][:2]
                retval = setscene_depvalid(datadep, taskdeps, [], d)
                if retval:
                    bb.note("Skipping setscene dependency %s for m4 macro copying" % datadep)
                    continue
                done.append(datadep)
                new.append(datadep)
                configuredeps.append(setscenedeps[datadep][0])
        next = new

    cp = []
    if nodeps:
        bb.warn("autotools: Unable to find task dependencies, -b being used? Pulling in all m4 files")
        for l in [d.expand("${STAGING_DATADIR_NATIVE}/aclocal/"), d.expand("${STAGING_DATADIR}/aclocal/")]:
            cp.extend(os.path.join(l, f) for f in os.listdir(l))

    for c in configuredeps:
        if c.endswith("-native"):
            manifest = d.expand("${SSTATE_MANIFESTS}/manifest-${BUILD_ARCH}-%s.populate_sysroot" % c)
        elif c.startswith("nativesdk-"):
            manifest = d.expand("${SSTATE_MANIFESTS}/manifest-${SDK_ARCH}_${SDK_OS}-%s.populate_sysroot" % c)
        elif "-cross-" in c or "-crosssdk" in c:
            continue
        else:
            manifest = d.expand("${SSTATE_MANIFESTS}/manifest-${MACHINE}-%s.populate_sysroot" % c)
        try:
            f = open(manifest, "r")
            for l in f:
                if "/aclocal/" in l and l.strip().endswith(".m4"):
                    cp.append(l.strip())
                elif "config_site.d/" in l:
                    cp.append(l.strip())
        except:
            bb.warn("%s not found" % manifest)

    for c in cp:
        t = os.path.join(aclocaldir, os.path.basename(c))
        if not os.path.exists(t):
            os.symlink(c, t)

    # Refresh variable with cache files
    d.setVar("CONFIG_SITE", siteinfo_get_files(d, aclocalcache=True))
}
autotools_copy_aclocals[vardepsexclude] += "MACHINE SDK_ARCH BUILD_ARCH SDK_OS BB_TASKDEPDATA"

CONFIGURE_FILES = "${S}/configure.in ${S}/configure.ac ${S}/config.h.in ${S}/acinclude.m4 Makefile.am"

autotools_do_configure() {
	# WARNING: gross hack follows:
	# An autotools built package generally needs these scripts, however only
	# automake or libtoolize actually install the current versions of them.
	# This is a problem in builds that do not use libtool or automake, in the case
	# where we -need- the latest version of these scripts.  e.g. running a build
	# for a package whose autotools are old, on an x86_64 machine, which the old
	# config.sub does not support.  Work around this by installing them manually
	# regardless.

	PRUNE_M4=""

	for ac in `find ${S} -ignore_readdir_race -name configure.in -o -name configure.ac`; do
		rm -f `dirname $ac`/configure
	done
	if [ -e ${AUTOTOOLS_SCRIPT_PATH}/configure.in -o -e ${AUTOTOOLS_SCRIPT_PATH}/configure.ac ]; then
		olddir=`pwd`
		cd ${AUTOTOOLS_SCRIPT_PATH}
		ACLOCAL="aclocal --system-acdir=${ACLOCALDIR}/"
		if [ x"${acpaths}" = xdefault ]; then
			acpaths=
			for i in `find ${AUTOTOOLS_SCRIPT_PATH} -ignore_readdir_race -maxdepth 2 -name \*.m4|grep -v 'aclocal.m4'| \
				grep -v 'acinclude.m4' | sed -e 's,\(.*/\).*$,\1,'|sort -u`; do
				acpaths="$acpaths -I $i"
			done
		else
			acpaths="${acpaths}"
		fi
		AUTOV=`automake --version | sed -e '1{s/.* //;s/\.[0-9]\+$//};q'`
		automake --version
		echo "AUTOV is $AUTOV"
		if [ -d ${STAGING_DATADIR_NATIVE}/aclocal-$AUTOV ]; then
			ACLOCAL="$ACLOCAL --automake-acdir=${STAGING_DATADIR_NATIVE}/aclocal-$AUTOV"
		fi
		# autoreconf is too shy to overwrite aclocal.m4 if it doesn't look
		# like it was auto-generated.  Work around this by blowing it away
		# by hand, unless the package specifically asked not to run aclocal.
		if ! echo ${EXTRA_AUTORECONF} | grep -q "aclocal"; then
			rm -f aclocal.m4
		fi
		if [ -e configure.in ]; then
			CONFIGURE_AC=configure.in
		else
			CONFIGURE_AC=configure.ac
		fi
		if grep "^[[:space:]]*AM_GLIB_GNU_GETTEXT" $CONFIGURE_AC >/dev/null; then
			if grep "sed.*POTFILES" $CONFIGURE_AC >/dev/null; then
				: do nothing -- we still have an old unmodified configure.ac
	    		else
				bbnote Executing glib-gettextize --force --copy
				echo "no" | glib-gettextize --force --copy
			fi
		elif grep "^[[:space:]]*AM_GNU_GETTEXT" $CONFIGURE_AC >/dev/null; then
			# We'd call gettextize here if it wasn't so broken...
			cp ${STAGING_DATADIR_NATIVE}/gettext/config.rpath ${AUTOTOOLS_AUXDIR}/
			if [ -d ${S}/po/ ]; then
				cp -f ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in ${S}/po/
				if [ ! -e ${S}/po/remove-potcdate.sin ]; then
					cp ${STAGING_DATADIR_NATIVE}/gettext/po/remove-potcdate.sin ${S}/po/
				fi
			fi
			PRUNE_M4="$PRUNE_M4 gettext.m4 iconv.m4 lib-ld.m4 lib-link.m4 lib-prefix.m4 nls.m4 po.m4 progtest.m4"
		fi
		mkdir -p m4
		if grep "^[[:space:]]*[AI][CT]_PROG_INTLTOOL" $CONFIGURE_AC >/dev/null; then
			if ! echo "${DEPENDS}" | grep -q intltool-native; then
				bbwarn "Missing DEPENDS on intltool-native"
			fi
			PRUNE_M4="$PRUNE_M4 intltool.m4"
			bbnote Executing intltoolize --copy --force --automake
			intltoolize --copy --force --automake
		fi

		for i in $PRUNE_M4; do
			find ${S} -ignore_readdir_race -name $i -delete
		done

		bbnote Executing ACLOCAL=\"$ACLOCAL\" autoreconf --verbose --install --force ${EXTRA_AUTORECONF} $acpaths
		ACLOCAL="$ACLOCAL" autoreconf -Wcross --verbose --install --force ${EXTRA_AUTORECONF} $acpaths || die "autoreconf execution failed."
		cd $olddir
	fi
	if [ -e ${CONFIGURE_SCRIPT} ]; then
		oe_runconf
	else
		bbnote "nothing to configure"
	fi
}

autotools_do_compile() {
    oe_runmake
}

autotools_do_install() {
	oe_runmake 'DESTDIR=${D}' install
	# Info dir listing isn't interesting at this point so remove it if it exists.
	if [ -e "${D}${infodir}/dir" ]; then
		rm -f ${D}${infodir}/dir
	fi
}

inherit siteconfig

EXPORT_FUNCTIONS do_configure do_compile do_install

B = "${WORKDIR}/build"
