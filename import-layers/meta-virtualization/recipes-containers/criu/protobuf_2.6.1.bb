SUMMARY = "protobuf"
DESCRIPTION = "Protocol Buffers are a way of encoding structured data in \
an efficient yet extensible format. Google uses Protocol Buffers for \
almost all of its internal RPC protocols and file formats."
HOMEPAGE = "http://code.google.com/p/protobuf/"
SECTION = "console/tools"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=af6809583bfde9a31595a58bb4a24514"

PR = "r0"
EXCLUDE_FROM_WORLD = "1"

SRC_URI[md5sum] = "af05b2cca289f7b86eef2734a0cdc8b9"
SRC_URI[sha256sum] = "2667b7cda4a6bc8a09e5463adf3b5984e08d94e72338277affa8594d8b6e5cd1"
SRC_URI = "https://github.com/google/protobuf/archive/v2.6.1.tar.gz;downloadfilename=protobuf-2.6.1.tar.gz\
	file://protobuf-allow-running-python-scripts-from-anywhere.patch \
	file://Omit-google-apputils-dependency.patch \
	file://run-ptest"

COMPATIBLE_HOST = "(x86_64|arm|aarch64).*-linux"

EXTRA_OECONF += " --with-protoc=${STAGING_BINDIR_NATIVE}/protoc"
inherit autotools setuptools ptest

DEPENDS += "protobuf-native"

PYTHON_SRC_DIR="python"
TEST_SRC_DIR="examples"
LANG_SUPPORT="cpp python"

do_compile() {
	# Compile protoc compiler
	base_do_compile
}

do_compile_ptest() {
	# Modify makefile to use the cross-compiler
	sed -e "s|c++|${CXX}|g" -i "${S}/${TEST_SRC_DIR}/Makefile"

	mkdir -p "${B}/${TEST_SRC_DIR}"

	# Add the location of the cross-compiled header and library files
	# which haven't been installed yet.
	cp "${B}/protobuf.pc" "${B}/${TEST_SRC_DIR}/protobuf.pc"
	sed -e 's|libdir=|libdir=${PKG_CONFIG_SYSROOT_DIR}|' -i "${B}/${TEST_SRC_DIR}/protobuf.pc"
	sed -e 's|Cflags:|Cflags: -I${S}/src|' -i "${B}/${TEST_SRC_DIR}/protobuf.pc"
	sed -e 's|Libs:|Libs: -L${B}/src/.libs|' -i "${B}/${TEST_SRC_DIR}/protobuf.pc"
	export PKG_CONFIG_PATH="${B}/${TEST_SRC_DIR}"

	# Save the pkgcfg sysroot variable, and update it to nothing so
	# that it doesn't append the sysroot to the beginning of paths.
	# The header and library files aren't installed to the target
	# system yet.  So the absolute paths were specified above.
	save_pkg_config_sysroot_dir=$PKG_CONFIG_SYSROOT_DIR
	export PKG_CONFIG_SYSROOT_DIR=

	# Compile the tests
	for lang in ${LANG_SUPPORT}; do
		oe_runmake -C "${S}/${TEST_SRC_DIR}" ${lang}
	done

	# Restore the pkgconfig sysroot variable
	export PKG_CONFIG_SYSROOT_DIR=$save_pkg_config_sysroot_dir
}

do_install() {
	local olddir=`pwd`

	# Install protoc compiler
	autotools_do_install

	# Install header files
	export PROTOC="${STAGING_BINDIR_NATIVE}/protoc"
	cd "${S}/${PYTHON_SRC_DIR}"
	distutils_do_install

	cd "$olddir"
}

do_install_ptest() {
	local olddir=`pwd`

	cd "${S}/${TEST_SRC_DIR}"
	install -d "${D}/${PTEST_PATH}"
	for i in add_person* list_people*; do
		if [ -x "$i" ]; then
			install "$i" "${D}/${PTEST_PATH}"
		fi
	done
	cp "${S}/${TEST_SRC_DIR}/addressbook_pb2.py" "${D}/${PTEST_PATH}"
	
	cd "$olddir"
}

BBCLASSEXTEND = "nativesdk"

