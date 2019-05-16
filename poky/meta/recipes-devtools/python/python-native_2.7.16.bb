require python.inc
EXTRANATIVEPATH += "bzip2-native"
DEPENDS = "openssl-native bzip2-replacement-native zlib-native readline-native sqlite3-native expat-native gdbm-native db-native"

SRC_URI += "\
            file://05-enable-ctypes-cross-build.patch \
            file://10-distutils-fix-swig-parameter.patch \
            file://11-distutils-never-modify-shebang-line.patch \
            file://0001-distutils-set-the-prefix-to-be-inside-staging-direct.patch \
            file://debug.patch \
            file://unixccompiler.patch \
            file://nohostlibs.patch \
            file://multilib.patch \
            file://add-md5module-support.patch \
            file://builddir.patch \
            file://parallel-makeinst-create-bindir.patch \
            file://revert_use_of_sysconfigdata.patch \
            file://0001-python-native-fix-one-do_populate_sysroot-warning.patch \
           "

S = "${WORKDIR}/Python-${PV}"

FILESEXTRAPATHS =. "${FILE_DIRNAME}/${PN}:"

inherit native

EXTRA_OECONF_append = " --bindir=${bindir}/${PN} --with-system-expat=${STAGING_DIR_HOST}"

EXTRA_OEMAKE = '\
  LIBC="" \
  STAGING_LIBDIR=${STAGING_LIBDIR_NATIVE} \
  STAGING_INCDIR=${STAGING_INCDIR_NATIVE} \
'

do_configure_append() {
	autoreconf --verbose --install --force --exclude=autopoint ../Python-${PV}/Modules/_ctypes/libffi
}

# Regenerate all of the generated files
# This ensures that pgen and friends get created during the compile phase
do_compile_prepend() {
    oe_runmake regen-all
}

do_install() {
	oe_runmake 'DESTDIR=${D}' install
	install -d ${D}${bindir}/${PN}
	install -m 0755 Parser/pgen ${D}${bindir}/${PN}

	# Make sure we use /usr/bin/env python
	for PYTHSCRIPT in `grep -rIl ${bindir}/${PN}/python ${D}${bindir}/${PN}`; do
		sed -i -e '1s|^#!.*|#!/usr/bin/env python|' $PYTHSCRIPT
	done

	# Add a symlink to the native Python so that scripts can just invoke
	# "nativepython" and get the right one without needing absolute paths
	# (these often end up too long for the #! parser in the kernel as the
	# buffer is 128 bytes long).
	ln -s python-native/python ${D}${bindir}/nativepython

	# We don't want modules in ~/.local being used in preference to those
	# installed in the native sysroot, so disable user site support.
	sed -i -e 's,^\(ENABLE_USER_SITE = \).*,\1False,' ${D}${libdir}/python${PYTHON_MAJMIN}/site.py
}

python(){

    # Read JSON manifest
    import json
    pythondir = d.getVar('THISDIR')
    with open(pythondir+'/python/python2-manifest.json') as manifest_file:
        manifest_str =  manifest_file.read()
        json_start = manifest_str.find('# EOC') + 6
        manifest_file.seek(json_start)
        manifest_str = manifest_file.read()
        python_manifest = json.loads(manifest_str)

    rprovides = d.getVar('RPROVIDES').split()

    # Hardcoded since it cant be python-native-foo, should be python-foo-native
    pn = 'python'

    for key in python_manifest:
        pypackage = pn + '-' + key + '-native'
        if pypackage not in rprovides:
              rprovides.append(pypackage)

    d.setVar('RPROVIDES', ' '.join(rprovides))
}
