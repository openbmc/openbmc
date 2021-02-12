SUMMARY = "The Python Programming Language"
HOMEPAGE = "http://www.python.org"
LICENSE = "PSFv2"
SECTION = "devel/python"

LIC_FILES_CHKSUM = "file://LICENSE;md5=33223c9ef60c31e3f0e866cb09b65e83"

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
           file://0001-Makefile-fix-Issue36464-parallel-build-race-problem.patch \
           file://0001-bpo-36852-proper-detection-of-mips-architecture-for-.patch \
           file://crosspythonpath.patch \
           file://0001-Use-FLAG_REF-always-for-interned-strings.patch \
           file://0001-test_locale.py-correct-the-test-output-format.patch \
           file://0017-setup.py-do-not-report-missing-dependencies-for-disa.patch \
           file://0001-setup.py-pass-missing-libraries-to-Extension-for-mul.patch \
           file://0001-Makefile-do-not-compile-.pyc-in-parallel.patch \
           file://0020-configure.ac-setup.py-do-not-add-a-curses-include-pa.patch \
           file://0001-Lib-sysconfig.py-use-libdir-values-from-configuratio.patch \
           file://CVE-2021-3177.patch \
           "

SRC_URI_append_class-native = " \
           file://0001-distutils-sysconfig-append-STAGING_LIBDIR-python-sys.patch \
           file://12-distutils-prefix-is-inside-staging-area.patch \
           file://0001-Don-t-search-system-for-headers-libraries.patch \
           "
SRC_URI[sha256sum] = "991c3f8ac97992f3d308fefeb03a64db462574eadbff34ce8bc5bb583d9903ff"

# exclude pre-releases for both python 2.x and 3.x
UPSTREAM_CHECK_REGEX = "[Pp]ython-(?P<pver>\d+(\.\d+)+).tar"
UPSTREAM_CHECK_URI = "https://www.python.org/downloads/source/"

CVE_PRODUCT = "python"

# Upstream consider this expected behaviour
CVE_CHECK_WHITELIST += "CVE-2007-4559"
# This is not exploitable when glibc has CVE-2016-10739 fixed.
CVE_CHECK_WHITELIST += "CVE-2019-18348"

# This is windows only issue.
CVE_CHECK_WHITELIST += "CVE-2020-15523"

PYTHON_MAJMIN = "3.9"

S = "${WORKDIR}/Python-${PV}"

BBCLASSEXTEND = "native nativesdk"

inherit autotools pkgconfig qemu ptest multilib_header update-alternatives

MULTILIB_SUFFIX = "${@d.getVar('base_libdir',1).split('/')[-1]}"

ALTERNATIVE_${PN}-dev = "python3-config"
ALTERNATIVE_LINK_NAME[python3-config] = "${bindir}/python${PYTHON_MAJMIN}-config"
ALTERNATIVE_TARGET[python3-config] = "${bindir}/python${PYTHON_MAJMIN}-config-${MULTILIB_SUFFIX}"


DEPENDS = "bzip2-replacement-native libffi bzip2 openssl sqlite3 zlib virtual/libintl xz virtual/crypt util-linux libtirpc libnsl2"
DEPENDS_append_class-target = " python3-native"
DEPENDS_append_class-nativesdk = " python3-native"

EXTRA_OECONF = " --without-ensurepip --enable-shared --with-platlibdir=${baselib}"
EXTRA_OECONF_append_class-native = " --bindir=${bindir}/${PN}"

export CROSSPYTHONPATH="${STAGING_LIBDIR_NATIVE}/python${PYTHON_MAJMIN}/lib-dynload/"

EXTRANATIVEPATH += "python3-native"

# LTO will be enabled via packageconfig depending upong distro features
LTO_class-target = ""

CACHED_CONFIGUREVARS = " \
                ac_cv_file__dev_ptmx=yes \
                ac_cv_file__dev_ptc=no \
                ac_cv_working_tzset=yes \
"

def possibly_include_pgo(d):
    # PGO currently causes builds to not be reproducible, so disable it for
    # now. See YOCTO #13407
    if bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', True, False, d) and d.getVar('BUILD_REPRODUCIBLE_BINARIES') != '1':
        return 'pgo'
    
    return ''

PACKAGECONFIG_class-target ??= "readline ${@possibly_include_pgo(d)} gdbm ${@bb.utils.filter('DISTRO_FEATURES', 'lto', d)}"
PACKAGECONFIG_class-native ??= "readline gdbm"
PACKAGECONFIG_class-nativesdk ??= "readline gdbm"
PACKAGECONFIG[readline] = ",,readline"
# Use profile guided optimisation by running PyBench inside qemu-user
PACKAGECONFIG[pgo] = "--enable-optimizations,,qemu-native"
PACKAGECONFIG[tk] = ",,tk"
PACKAGECONFIG[gdbm] = ",,gdbm"
PACKAGECONFIG[lto] = "--with-lto,,"

do_configure_prepend () {
    mkdir -p ${B}/Modules
    cat > ${B}/Modules/Setup.local << EOF
*disabled*
${@bb.utils.contains('PACKAGECONFIG', 'gdbm', '', '_gdbm _dbm', d)}
${@bb.utils.contains('PACKAGECONFIG', 'readline', '', 'readline', d)}
EOF
}

CPPFLAGS_append = " -I${STAGING_INCDIR}/ncursesw -I${STAGING_INCDIR}/uuid"

EXTRA_OEMAKE = '\
  STAGING_LIBDIR=${STAGING_LIBDIR} \
  STAGING_INCDIR=${STAGING_INCDIR} \
  LIB=${baselib} \
'

do_compile_prepend_class-target() {
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

do_install_prepend() {
        ${WORKDIR}/check_build_completeness.py ${T}/log.do_compile
}

do_install_append_class-target() {
        oe_multilib_header python${PYTHON_MAJMIN}/pyconfig.h
}

do_install_append_class-native() {
        # Make sure we use /usr/bin/env python
        for PYTHSCRIPT in `grep -rIl ${bindir}/${PN}/python ${D}${bindir}/${PN}`; do
                sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' $PYTHSCRIPT
        done
        # Add a symlink to the native Python so that scripts can just invoke
        # "nativepython" and get the right one without needing absolute paths
        # (these often end up too long for the #! parser in the kernel as the
        # buffer is 128 bytes long).
        ln -s python3-native/python3 ${D}${bindir}/nativepython3
}

do_install_append() {
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
        rm ${D}${libdir}/python${PYTHON_MAJMIN}/test/__pycache__/test_range.cpython*
        rm ${D}${libdir}/python${PYTHON_MAJMIN}/test/__pycache__/test_xml_etree.cpython*
}

do_install_append_class-nativesdk () {
    create_wrapper ${D}${bindir}/python${PYTHON_MAJMIN} TERMINFO_DIRS='${sysconfdir}/terminfo:/etc/terminfo:/usr/share/terminfo:/usr/share/misc/terminfo:/lib/terminfo' PYTHONNOUSERSITE='1'
}

SSTATE_SCAN_FILES += "Makefile _sysconfigdata.py"
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

    d.setVar('RPROVIDES_class-native', ' '.join(rprovides))

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
        d.setVar('FILES_' + pypackage, '')
        for value in python_manifest[key]['files']:
            d.appendVar('FILES_' + pypackage, ' ' + value)

        # Add cached files
        if include_pycs == '1':
            for value in python_manifest[key]['cached']:
                    d.appendVar('FILES_' + pypackage, ' ' + value)

        for value in python_manifest[key]['rdepends']:
            # Make it work with or without $PN
            if '${PN}' in value:
                value=value.split('-', 1)[1]
            d.appendVar('RDEPENDS_' + pypackage, ' ' + pn + '-' + value)

        for value in python_manifest[key].get('rrecommends', ()):
            if '${PN}' in value:
                value=value.split('-', 1)[1]
            d.appendVar('RRECOMMENDS_' + pypackage, ' ' + pn + '-' + value)

        d.setVar('SUMMARY_' + pypackage, python_manifest[key]['summary'])

    # Prepending so to avoid python-misc getting everything
    packages = newpackages + packages
    d.setVar('PACKAGES', ' '.join(packages))
    d.setVar('ALLOW_EMPTY_${PN}-modules', '1')
    d.setVar('ALLOW_EMPTY_${PN}-pkgutil', '1')
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
RRECOMMENDS_${PN}-core_append_class-nativesdk = " nativesdk-python3-modules"
RRECOMMENDS_${PN}-crypt_append_class-target = " ${MLPREFIX}openssl ${MLPREFIX}ca-certificates"
RRECOMMENDS_${PN}-crypt_append_class-nativesdk = " ${MLPREFIX}openssl ${MLPREFIX}ca-certificates"

# For historical reasons PN is empty and provided by python3-modules
FILES_${PN} = ""
RPROVIDES_${PN}-modules = "${PN}"

FILES_${PN}-pydoc += "${bindir}/pydoc${PYTHON_MAJMIN} ${bindir}/pydoc3"
FILES_${PN}-idle += "${bindir}/idle3 ${bindir}/idle${PYTHON_MAJMIN}"

# provide python-pyvenv from python3-venv
RPROVIDES_${PN}-venv += "${MLPREFIX}python3-pyvenv"

# package libpython3
PACKAGES =+ "libpython3 libpython3-staticdev"
FILES_libpython3 = "${libdir}/libpython*.so.*"
FILES_libpython3-staticdev += "${libdir}/python${PYTHON_MAJMIN}/config-${PYTHON_MAJMIN}-*/libpython${PYTHON_MAJMIN}.a"
INSANE_SKIP_${PN}-dev += "dev-elf"

# catch all the rest (unsorted)
PACKAGES += "${PN}-misc"
RDEPENDS_${PN}-misc += "\
  ${PN}-core \
  ${PN}-email \
  ${PN}-codecs \
  ${PN}-pydoc \
  ${PN}-pickle \
  ${PN}-audio \
  ${PN}-numbers \
"
RDEPENDS_${PN}-modules_append_class-target = " ${MLPREFIX}python3-misc"
RDEPENDS_${PN}-modules_append_class-nativesdk = " ${MLPREFIX}python3-misc"
FILES_${PN}-misc = "${libdir}/python${PYTHON_MAJMIN} ${libdir}/python${PYTHON_MAJMIN}/lib-dynload"

# catch manpage
PACKAGES += "${PN}-man"
FILES_${PN}-man = "${datadir}/man"

# See https://bugs.python.org/issue18748 and https://bugs.python.org/issue37395
RDEPENDS_libpython3_append_libc-glibc = " libgcc"
RDEPENDS_${PN}-ctypes_append_libc-glibc = " ${MLPREFIX}ldconfig"
RDEPENDS_${PN}-ptest = "${PN}-modules ${PN}-tests unzip bzip2 libgcc tzdata-europe coreutils sed"
RDEPENDS_${PN}-ptest_append_libc-glibc = " locale-base-tr-tr.iso-8859-9"
RDEPENDS_${PN}-tkinter += "${@bb.utils.contains('PACKAGECONFIG', 'tk', 'tk tk-lib', '', d)}"
RDEPENDS_${PN}-idle += "${@bb.utils.contains('PACKAGECONFIG', 'tk', '${PN}-tkinter tcl', '', d)}"
RDEPENDS_${PN}-dev = ""

RDEPENDS_${PN}-tests_append_class-target = " ${MLPREFIX}bash"
RDEPENDS_${PN}-tests_append_class-nativesdk = " ${MLPREFIX}bash"

# Python's tests contain large numbers of files we don't need in the recipe sysroots
SYSROOT_PREPROCESS_FUNCS += " py3_sysroot_cleanup"
py3_sysroot_cleanup () {
	rm -rf ${SYSROOT_DESTDIR}${libdir}/python${PYTHON_MAJMIN}/test
}
