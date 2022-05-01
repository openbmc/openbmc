SUMMARY = "The Python Programming Language"
HOMEPAGE = "http://www.python.org"
DESCRIPTION = "Python is a programming language that lets you work more quickly and integrate your systems more effectively."
LICENSE = "PSF-2.0"
SECTION = "devel/python"

LIC_FILES_CHKSUM = "file://LICENSE;md5=4b8801e752a2c70ac41a5f9aa243f766"

SRC_URI = "http://www.python.org/ftp/python/${PV}/Python-${PV}.tar.xz \
           file://run-ptest \
           file://create_manifest3.py \
           file://get_module_deps3.py \
           file://python3-manifest.json \
           file://check_build_completeness.py \
           file://reformat_sysconfig.py \
           file://cgi_py.patch \
           file://0001-Do-not-add-usr-lib-termcap-to-linker-flags-to-avoid-.patch \
           ${@bb.utils.contains('PACKAGECONFIG', 'tk', '', 'file://avoid_warning_about_tkinter.patch', d)} \
           file://0001-Do-not-use-the-shell-version-of-python-config-that-w.patch \
           file://python-config.patch \
           file://0001-Makefile.pre-use-qemu-wrapper-when-gathering-profile.patch \
           file://0001-python3-use-cc_basename-to-replace-CC-for-checking-c.patch \
           file://0001-bpo-36852-proper-detection-of-mips-architecture-for-.patch \
           file://crosspythonpath.patch \
           file://0001-Use-FLAG_REF-always-for-interned-strings.patch \
           file://0001-test_locale.py-correct-the-test-output-format.patch \
           file://0017-setup.py-do-not-report-missing-dependencies-for-disa.patch \
           file://0001-Makefile-do-not-compile-.pyc-in-parallel.patch \
           file://0020-configure.ac-setup.py-do-not-add-a-curses-include-pa.patch \
           file://0001-Skip-failing-tests-due-to-load-variability-on-YP-AB.patch \
           file://0001-test_ctypes.test_find-skip-without-tools-sdk.patch \
           file://makerace.patch \
           file://0001-sysconfig.py-use-platlibdir-also-for-purelib.patch \
           file://0001-Lib-pty.py-handle-stdin-I-O-errors-same-way-as-maste.patch \
           file://0001-setup.py-Do-not-detect-multiarch-paths-when-cross-co.patch \
           "

SRC_URI:append:class-native = " \
           file://0001-Lib-sysconfig.py-use-prefix-value-from-build-configu.patch \
           file://0001-distutils-sysconfig-append-STAGING_LIBDIR-python-sys.patch \
           file://12-distutils-prefix-is-inside-staging-area.patch \
           file://0001-Don-t-search-system-for-headers-libraries.patch \
           "
SRC_URI[sha256sum] = "80bf925f571da436b35210886cf79f6eb5fa5d6c571316b73568343451f77a19"

# exclude pre-releases for both python 2.x and 3.x
UPSTREAM_CHECK_REGEX = "[Pp]ython-(?P<pver>\d+(\.\d+)+).tar"
UPSTREAM_CHECK_URI = "https://www.python.org/downloads/source/"

CVE_PRODUCT = "python"

# Upstream consider this expected behaviour
CVE_CHECK_IGNORE += "CVE-2007-4559"
# This is not exploitable when glibc has CVE-2016-10739 fixed.
CVE_CHECK_IGNORE += "CVE-2019-18348"
# These are specific to Microsoft Windows
CVE_CHECK_IGNORE += "CVE-2020-15523 CVE-2022-26488"

PYTHON_MAJMIN = "3.10"

S = "${WORKDIR}/Python-${PV}"

BBCLASSEXTEND = "native nativesdk"

inherit autotools pkgconfig qemu ptest multilib_header update-alternatives

MULTILIB_SUFFIX = "${@d.getVar('base_libdir',1).split('/')[-1]}"

ALTERNATIVE:${PN}-dev = "python3-config"
ALTERNATIVE_LINK_NAME[python3-config] = "${bindir}/python${PYTHON_MAJMIN}-config"
ALTERNATIVE_TARGET[python3-config] = "${bindir}/python${PYTHON_MAJMIN}-config-${MULTILIB_SUFFIX}"


DEPENDS = "bzip2-replacement-native libffi bzip2 openssl sqlite3 zlib virtual/libintl xz virtual/crypt util-linux-libuuid libtirpc libnsl2 autoconf-archive-native ncurses"
DEPENDS:append:class-target = " python3-native"
DEPENDS:append:class-nativesdk = " python3-native"

# force to use the mutex+cond implementation (https://bugs.python.org/issue41710)
CFLAGS += "-DHAVE_BROKEN_POSIX_SEMAPHORES"

EXTRA_OECONF = " --without-ensurepip --enable-shared --with-platlibdir=${baselib}"
EXTRA_OECONF:append:class-native = " --bindir=${bindir}/${PN}"

export CROSSPYTHONPATH="${STAGING_LIBDIR_NATIVE}/python${PYTHON_MAJMIN}/lib-dynload/"

EXTRANATIVEPATH += "python3-native"

# LTO will be enabled via packageconfig depending upong distro features
LTO:class-target = ""

CACHED_CONFIGUREVARS = " \
                ac_cv_file__dev_ptmx=yes \
                ac_cv_file__dev_ptc=no \
                ac_cv_working_tzset=yes \
"

# PGO currently causes builds to not be reproducible so disable by default, see YOCTO #13407
PACKAGECONFIG:class-target ??= "readline gdbm ${@bb.utils.filter('DISTRO_FEATURES', 'lto', d)}"
PACKAGECONFIG:class-native ??= "readline gdbm"
PACKAGECONFIG:class-nativesdk ??= "readline gdbm"
PACKAGECONFIG[readline] = ",,readline"
# Use profile guided optimisation by running PyBench inside qemu-user
PACKAGECONFIG[pgo] = "--enable-optimizations,,qemu-native"
PACKAGECONFIG[tk] = ",,tk"
PACKAGECONFIG[gdbm] = ",,gdbm"
PACKAGECONFIG[lto] = "--with-lto,,"

do_configure:prepend () {
    mkdir -p ${B}/Modules
    cat > ${B}/Modules/Setup.local << EOF
*disabled*
${@bb.utils.contains('PACKAGECONFIG', 'gdbm', '', '_gdbm _dbm', d)}
${@bb.utils.contains('PACKAGECONFIG', 'readline', '', 'readline', d)}
EOF
}

CPPFLAGS:append = " -I${STAGING_INCDIR}/ncursesw -I${STAGING_INCDIR}/uuid"

EXTRA_OEMAKE = '\
  STAGING_LIBDIR=${STAGING_LIBDIR} \
  STAGING_INCDIR=${STAGING_INCDIR} \
  LIB=${baselib} \
'

do_compile:prepend:class-target() {
       if ${@bb.utils.contains('PACKAGECONFIG', 'pgo', 'true', 'false', d)}; then
                qemu_binary="${@qemu_wrapper_cmdline(d, '${STAGING_DIR_TARGET}', ['${B}', '${STAGING_DIR_TARGET}/${base_libdir}'])}"
                cat >pgo-wrapper <<EOF
#!/bin/sh
cd ${B}
$qemu_binary "\$@"
EOF
                chmod +x pgo-wrapper
        fi
}

do_install:prepend() {
        ${WORKDIR}/check_build_completeness.py ${T}/log.do_compile
}

do_install:append:class-target() {
        oe_multilib_header python${PYTHON_MAJMIN}/pyconfig.h
}

do_install:append:class-native() {
        # Make sure we use /usr/bin/env python
        for PYTHSCRIPT in `grep -rIl ${bindir}/${PN}/python ${D}${bindir}/${PN}`; do
                sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' $PYTHSCRIPT
        done
        # Add a symlink to the native Python so that scripts can just invoke
        # "nativepython" and get the right one without needing absolute paths
        # (these often end up too long for the #! parser in the kernel as the
        # buffer is 128 bytes long).
        ln -s python3-native/python3 ${D}${bindir}/nativepython3

        # Remove the opt-1.pyc and opt-2.pyc files. There are over 3,000 of them
        # and the overhead in each recipe-sysroot-native isn't worth it, particularly
        # when they're only used for python called with -O or -OO.
        #find ${D} -name *opt-*.pyc -delete
        # Remove all pyc files. There are a ton of them and it is probably faster to let
        # python create the ones it wants at runtime rather than manage in the sstate 
        # tarballs and sysroot creation.
        find ${D} -name *.pyc -delete

}

do_install:append() {
        for c in ${D}/${libdir}/python${PYTHON_MAJMIN}/_sysconfigdata*.py; do
            python3 ${WORKDIR}/reformat_sysconfig.py $c
        done
        rm -f ${D}${libdir}/python${PYTHON_MAJMIN}/__pycache__/_sysconfigdata*.cpython*

        mkdir -p ${D}${libdir}/python-sysconfigdata
        sysconfigfile=`find ${D} -name _sysconfig*.py`
        cp $sysconfigfile ${D}${libdir}/python-sysconfigdata/_sysconfigdata.py

        sed -i  \
                -e "s,^ 'LIBDIR'.*, 'LIBDIR': '${STAGING_LIBDIR}'\,,g" \
                -e "s,^ 'INCLUDEDIR'.*, 'INCLUDEDIR': '${STAGING_INCDIR}'\,,g" \
                -e "s,^ 'CONFINCLUDEDIR'.*, 'CONFINCLUDEDIR': '${STAGING_INCDIR}'\,,g" \
                -e "/^ 'INCLDIRSTOMAKE'/{N; s,/usr/include,${STAGING_INCDIR},g}" \
                -e "/^ 'INCLUDEPY'/s,/usr/include,${STAGING_INCDIR},g" \
                ${D}${libdir}/python-sysconfigdata/_sysconfigdata.py

        # Unfortunately the following pyc files are non-deterministc due to 'frozenset'
        # being written without strict ordering, even with PYTHONHASHSEED = 0
        # Upstream is discussing ways to solve the issue properly, until then let's
        # just not install the problematic files.
        # More info: http://benno.id.au/blog/2013/01/15/python-determinism
        rm -f ${D}${libdir}/python${PYTHON_MAJMIN}/test/__pycache__/test_range.cpython*
        rm -f ${D}${libdir}/python${PYTHON_MAJMIN}/test/__pycache__/test_xml_etree.cpython*

        # Remove the opt-1.pyc and opt-2.pyc files. They effectively waste space on embedded
        # style targets as they're only used when python is called with the -O or -OO options
        # which is rare.
        find ${D} -name *opt-*.pyc -delete
}

do_install:append:class-nativesdk () {
    # Make sure we use /usr/bin/env python
    for PYTHSCRIPT in `grep -rIl ${bindir}/python ${D}${bindir}`; do
         sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' $PYTHSCRIPT
    done
    create_wrapper ${D}${bindir}/python${PYTHON_MAJMIN} TERMINFO_DIRS='${sysconfdir}/terminfo:/etc/terminfo:/usr/share/terminfo:/usr/share/misc/terminfo:/lib/terminfo' PYTHONNOUSERSITE='1'
}

SSTATE_SCAN_FILES += "Makefile _sysconfigdata.py"
SSTATE_HASHEQUIV_FILEMAP = " \
    populate_sysroot:*/lib*/python3*/_sysconfigdata*.py:${TMPDIR} \
    populate_sysroot:*/lib*/python3*/_sysconfigdata*.py:${COREBASE} \
    populate_sysroot:*/lib*/python3*/config-*/Makefile:${TMPDIR} \
    populate_sysroot:*/lib*/python3*/config-*/Makefile:${COREBASE} \
    populate_sysroot:*/lib*/python-sysconfigdata/_sysconfigdata.py:${TMPDIR} \
    populate_sysroot:*/lib*/python-sysconfigdata/_sysconfigdata.py:${COREBASE} \
    "
PACKAGE_PREPROCESS_FUNCS += "py_package_preprocess"

py_package_preprocess () {
        # Remove references to buildmachine paths in target Makefile and _sysconfigdata
        sed -i -e 's:--sysroot=${STAGING_DIR_TARGET}::g' -e s:'--with-libtool-sysroot=${STAGING_DIR_TARGET}'::g \
                -e 's|${DEBUG_PREFIX_MAP}||g' \
                -e 's:${HOSTTOOLS_DIR}/::g' \
                -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
                -e 's:${RECIPE_SYSROOT}::g' \
                -e 's:${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}::g' \
                ${PKGD}/${libdir}/python${PYTHON_MAJMIN}/config-${PYTHON_MAJMIN}${PYTHON_ABI}*/Makefile \
                ${PKGD}/${libdir}/python${PYTHON_MAJMIN}/_sysconfigdata*.py \
                ${PKGD}/${bindir}/python${PYTHON_MAJMIN}-config

        # Reformat _sysconfigdata after modifying it so that it remains
        # reproducible
        for c in ${PKGD}/${libdir}/python${PYTHON_MAJMIN}/_sysconfigdata*.py; do
            python3 ${WORKDIR}/reformat_sysconfig.py $c
        done

        # Recompile _sysconfigdata after modifying it
        cd ${PKGD}
        sysconfigfile=`find . -name _sysconfigdata_*.py`
        ${STAGING_BINDIR_NATIVE}/python3-native/python3 \
             -c "from py_compile import compile; compile('$sysconfigfile')"
        ${STAGING_BINDIR_NATIVE}/python3-native/python3 \
             -c "from py_compile import compile; compile('$sysconfigfile', optimize=1)"
        ${STAGING_BINDIR_NATIVE}/python3-native/python3 \
             -c "from py_compile import compile; compile('$sysconfigfile', optimize=2)"
        cd -

        mv ${PKGD}/${bindir}/python${PYTHON_MAJMIN}-config ${PKGD}/${bindir}/python${PYTHON_MAJMIN}-config-${MULTILIB_SUFFIX}
        
        #Remove the unneeded copy of target sysconfig data
        rm -rf ${PKGD}/${libdir}/python-sysconfigdata
}

# We want bytecode precompiled .py files (.pyc's) by default
# but the user may set it on their own conf
INCLUDE_PYCS ?= "1"

python(){
    import collections, json

    filename = os.path.join(d.getVar('THISDIR'), 'python3', 'python3-manifest.json')
    # This python changes the datastore based on the contents of a file, so mark
    # that dependency.
    bb.parse.mark_dependency(d, filename)

    with open(filename) as manifest_file:
        manifest_str =  manifest_file.read()
        json_start = manifest_str.find('# EOC') + 6
        manifest_file.seek(json_start)
        manifest_str = manifest_file.read()
        python_manifest = json.loads(manifest_str, object_pairs_hook=collections.OrderedDict)

    # First set RPROVIDES for -native case
    # Hardcoded since it cant be python3-native-foo, should be python3-foo-native
    pn = 'python3'
    rprovides = (d.getVar('RPROVIDES') or "").split()

    # ${PN}-misc-native is not in the manifest
    rprovides.append(pn + '-misc-native')

    for key in python_manifest:
        pypackage = pn + '-' + key + '-native'
        if pypackage not in rprovides:
              rprovides.append(pypackage)

    d.setVar('RPROVIDES:class-native', ' '.join(rprovides))

    # Then work on the target
    include_pycs = d.getVar('INCLUDE_PYCS')

    packages = d.getVar('PACKAGES').split()
    pn = d.getVar('PN')

    newpackages=[]
    for key in python_manifest:
        pypackage = pn + '-' + key

        if pypackage not in packages:
            # We need to prepend, otherwise python-misc gets everything
            # so we use a new variable
            newpackages.append(pypackage)

        # "Build" python's manifest FILES, RDEPENDS and SUMMARY
        d.setVar('FILES:' + pypackage, '')
        for value in python_manifest[key]['files']:
            d.appendVar('FILES:' + pypackage, ' ' + value)

        # Add cached files
        if include_pycs == '1':
            for value in python_manifest[key]['cached']:
                    d.appendVar('FILES:' + pypackage, ' ' + value)

        for value in python_manifest[key]['rdepends']:
            # Make it work with or without $PN
            if '${PN}' in value:
                value=value.split('-', 1)[1]
            d.appendVar('RDEPENDS:' + pypackage, ' ' + pn + '-' + value)

        for value in python_manifest[key].get('rrecommends', ()):
            if '${PN}' in value:
                value=value.split('-', 1)[1]
            d.appendVar('RRECOMMENDS:' + pypackage, ' ' + pn + '-' + value)

        d.setVar('SUMMARY:' + pypackage, python_manifest[key]['summary'])

    # Prepending so to avoid python-misc getting everything
    packages = newpackages + packages
    d.setVar('PACKAGES', ' '.join(packages))
    d.setVar('ALLOW_EMPTY:${PN}-modules', '1')
    d.setVar('ALLOW_EMPTY:${PN}-pkgutil', '1')

    if "pgo" in d.getVar("PACKAGECONFIG").split() and not bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', True, False, d):
        bb.fatal("pgo cannot be enabled as there is no qemu-usermode support for this architecture/machine")
}

# Files needed to create a new manifest

do_create_manifest() {
    # This task should be run with every new release of Python.
    # We must ensure that PACKAGECONFIG enables everything when creating
    # a new manifest, this is to base our new manifest on a complete
    # native python build, containing all dependencies, otherwise the task
    # wont be able to find the required files.
    # e.g. BerkeleyDB is an optional build dependency so it may or may not
    # be present, we must ensure it is.

    cd ${WORKDIR}
    # This needs to be executed by python-native and NOT by HOST's python
    nativepython3 create_manifest3.py ${PYTHON_MAJMIN}
    cp python3-manifest.json.new ${THISDIR}/python3/python3-manifest.json
}

# bitbake python -c create_manifest
# Make sure we have native python ready when we create a new manifest
addtask do_create_manifest after do_patch do_prepare_recipe_sysroot

# manual dependency additions
RRECOMMENDS:${PN}-core:append:class-nativesdk = " nativesdk-python3-modules"
RRECOMMENDS:${PN}-crypt:append:class-target = " ${MLPREFIX}openssl ${MLPREFIX}ca-certificates"
RRECOMMENDS:${PN}-crypt:append:class-nativesdk = " ${MLPREFIX}openssl ${MLPREFIX}ca-certificates"

# For historical reasons PN is empty and provided by python3-modules
FILES:${PN} = ""
RPROVIDES:${PN}-modules = "${PN}"

FILES:${PN}-pydoc += "${bindir}/pydoc${PYTHON_MAJMIN} ${bindir}/pydoc3"
FILES:${PN}-idle += "${bindir}/idle3 ${bindir}/idle${PYTHON_MAJMIN}"

# provide python-pyvenv from python3-venv
RPROVIDES:${PN}-venv += "${MLPREFIX}python3-pyvenv"

# package libpython3
PACKAGES =+ "libpython3 libpython3-staticdev"
FILES:libpython3 = "${libdir}/libpython*.so.*"
FILES:libpython3-staticdev += "${libdir}/python${PYTHON_MAJMIN}/config-${PYTHON_MAJMIN}-*/libpython${PYTHON_MAJMIN}.a"
INSANE_SKIP:${PN}-dev += "dev-elf"
INSANE_SKIP:${PN}-ptest = "dev-deps"

# catch all the rest (unsorted)
PACKAGES += "${PN}-misc"
RDEPENDS:${PN}-misc += "\
  ${PN}-core \
  ${PN}-email \
  ${PN}-codecs \
  ${PN}-pydoc \
  ${PN}-pickle \
  ${PN}-audio \
  ${PN}-numbers \
"
RDEPENDS:${PN}-modules:append:class-target = " ${MLPREFIX}python3-misc"
RDEPENDS:${PN}-modules:append:class-nativesdk = " ${MLPREFIX}python3-misc"
FILES:${PN}-misc = "${libdir}/python${PYTHON_MAJMIN} ${libdir}/python${PYTHON_MAJMIN}/lib-dynload"

# catch manpage
PACKAGES += "${PN}-man"
FILES:${PN}-man = "${datadir}/man"

# See https://bugs.python.org/issue18748 and https://bugs.python.org/issue37395
RDEPENDS:libpython3:append:libc-glibc = " libgcc"
RDEPENDS:${PN}-ctypes:append:libc-glibc = " ${MLPREFIX}ldconfig"
RDEPENDS:${PN}-ptest = "${PN}-modules ${PN}-tests ${PN}-dev unzip bzip2 libgcc tzdata-europe coreutils sed"
RDEPENDS:${PN}-ptest:append:libc-glibc = " locale-base-tr-tr.iso-8859-9"
RDEPENDS:${PN}-tkinter += "${@bb.utils.contains('PACKAGECONFIG', 'tk', '${MLPREFIX}tk ${MLPREFIX}tk-lib', '', d)}"
RDEPENDS:${PN}-idle += "${@bb.utils.contains('PACKAGECONFIG', 'tk', '${PN}-tkinter ${MLPREFIX}tcl', '', d)}"
RDEPENDS:${PN}-dev = ""
RDEPENDS:${PN}-pydoc += "${PN}-io"

RDEPENDS:${PN}-tests:append:class-target = " ${MLPREFIX}bash"
RDEPENDS:${PN}-tests:append:class-nativesdk = " ${MLPREFIX}bash"

# Python's tests contain large numbers of files we don't need in the recipe sysroots
SYSROOT_PREPROCESS_FUNCS += " py3_sysroot_cleanup"
py3_sysroot_cleanup () {
	rm -rf ${SYSROOT_DESTDIR}${libdir}/python${PYTHON_MAJMIN}/test
}
