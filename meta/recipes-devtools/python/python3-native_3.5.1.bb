require recipes-devtools/python/python.inc

PR = "${INC_PR}.0"
PYTHON_MAJMIN = "3.5"
DISTRO_SRC_URI ?= "file://sitecustomize.py"
DISTRO_SRC_URI_linuxstdbase = ""
SRC_URI = "http://www.python.org/ftp/python/${PV}/Python-${PV}.tar.xz \
file://12-distutils-prefix-is-inside-staging-area.patch \
file://python-config.patch \
file://000-cross-compile.patch \
file://020-dont-compile-python-files.patch \
file://030-fixup-include-dirs.patch \
file://070-dont-clean-ipkg-install.patch \
file://080-distutils-dont_adjust_files.patch \
file://110-enable-zlib.patch \
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
"
SRC_URI[md5sum] = "e9ea6f2623fffcdd871b7b19113fde80"
SRC_URI[sha256sum] = "c6d57c0c366d9060ab6c0cdf889ebf3d92711d466cc0119c441dbf2746f725c9"

LIC_FILES_CHKSUM = "file://LICENSE;md5=dd98d01d471fac8d8dbdd975229dba03"

# exclude pre-releases for both python 2.x and 3.x
UPSTREAM_CHECK_REGEX = "[Pp]ython-(?P<pver>\d+(\.\d+)+).tar"

S = "${WORKDIR}/Python-${PV}"

EXTRANATIVEPATH += "bzip2-native"
DEPENDS = "openssl-native bzip2-replacement-native zlib-native readline-native sqlite3-native"

inherit native

RPROVIDES += "python3-distutils-native python3-compression-native python3-textutils-native python3-core-native"

EXTRA_OECONF_append = " --bindir=${bindir}/${PN} --without-ensurepip"

EXTRA_OEMAKE = '\
  BUILD_SYS="" \
  HOST_SYS="" \
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
}
