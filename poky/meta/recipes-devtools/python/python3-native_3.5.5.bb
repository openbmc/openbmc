require recipes-devtools/python/python.inc

PR = "${INC_PR}.0"
PYTHON_MAJMIN = "3.5"
DISTRO_SRC_URI ?= "file://sitecustomize.py"
DISTRO_SRC_URI_linuxstdbase = ""
SRC_URI = "http://www.python.org/ftp/python/${PV}/Python-${PV}.tar.xz \
file://12-distutils-prefix-is-inside-staging-area.patch \
file://python-config.patch \
file://0001-cross-compile-support.patch \
file://030-fixup-include-dirs.patch \
file://070-dont-clean-ipkg-install.patch \
file://080-distutils-dont_adjust_files.patch \
file://130-readline-setup.patch \
file://150-fix-setupterm.patch \
file://python-3.3-multilib.patch \
file://03-fix-tkinter-detection.patch \
file://avoid_warning_about_tkinter.patch \
file://shutil-follow-symlink-fix.patch \
file://0001-h2py-Fix-issue-13032-where-it-fails-with-UnicodeDeco.patch \
file://sysroot-include-headers.patch \
file://unixccompiler.patch \
${DISTRO_SRC_URI} \
file://sysconfig.py-add-_PYTHON_PROJECT_SRC.patch \
file://setup.py-check-cross_compiling-when-get-FLAGS.patch \
file://0001-Do-not-use-the-shell-version-of-python-config-that-w.patch \
file://support_SOURCE_DATE_EPOCH_in_py_compile.patch \
file://regen-all.patch \
"

SRC_URI[md5sum] = "f3763edf9824d5d3a15f5f646083b6e0"
SRC_URI[sha256sum] = "063d2c3b0402d6191b90731e0f735c64830e7522348aeb7ed382a83165d45009"

LIC_FILES_CHKSUM = "file://LICENSE;md5=b6ec515b22618f55fa07276b897bacea"

# exclude pre-releases for both python 2.x and 3.x
UPSTREAM_CHECK_REGEX = "[Pp]ython-(?P<pver>\d+(\.\d+)+).tar"

S = "${WORKDIR}/Python-${PV}"

EXTRANATIVEPATH += "bzip2-native"
DEPENDS = "openssl-native bzip2-replacement-native zlib-native readline-native sqlite3-native gdbm-native"

inherit native

EXTRA_OECONF_append = " --bindir=${bindir}/${PN} --without-ensurepip"

EXTRA_OEMAKE = '\
  LIBC="" \
  STAGING_LIBDIR=${STAGING_LIBDIR_NATIVE} \
  STAGING_INCDIR=${STAGING_INCDIR_NATIVE} \
  LIB=${baselib} \
  ARCH=${TARGET_ARCH} \
'

# No ctypes option for python 3
PYTHONLSBOPTS = ""

do_configure_append() {
	autoreconf --verbose --install --force --exclude=autopoint ../Python-${PV}/Modules/_ctypes/libffi
	sed -i -e 's,#define HAVE_GETRANDOM 1,/\* #undef HAVE_GETRANDOM \*/,' ${B}/pyconfig.h
}

# Regenerate all of the generated files
# This ensures that pgen and friends get created during the compile phase
#
do_compile_prepend() {
    # Assuming https://bugs.python.org/issue33080 has been addressed in Makefile.
    oe_runmake regen-all
}

do_install() {
	install -d ${D}${libdir}/pkgconfig
	oe_runmake 'DESTDIR=${D}' install
	if [ -e ${WORKDIR}/sitecustomize.py ]; then
		install -m 0644 ${WORKDIR}/sitecustomize.py ${D}/${libdir}/python${PYTHON_MAJMIN}
	fi
	install -d ${D}${bindir}/${PN}
	install -m 0755 Parser/pgen ${D}${bindir}/${PN}

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

python(){

    # Read JSON manifest
    import json
    pythondir = d.getVar('THISDIR',True)
    with open(pythondir+'/python3/python3-manifest.json') as manifest_file:
        python_manifest=json.load(manifest_file)

    rprovides = d.getVar('RPROVIDES').split()

    # Hardcoded since it cant be python3-native-foo, should be python3-foo-native
    pn = 'python3'

    for key in python_manifest:
        pypackage = pn + '-' + key + '-native'
        if pypackage not in rprovides:
              rprovides.append(pypackage)

    d.setVar('RPROVIDES', ' '.join(rprovides))
}
