SUMMARY = "Protocol Buffers - structured data serialisation mechanism"
DESCRIPTION = "Protocol Buffers are a way of encoding structured data in an \
efficient yet extensible format. Google uses Protocol Buffers for almost \
all of its internal RPC protocols and file formats."
HOMEPAGE = "https://github.com/google/protobuf"
SECTION = "console/tools"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=37b5762e07f0af8c74ce80a8bda4266b"

DEPENDS = "zlib"
DEPENDS:append:class-target = " protobuf-native"

SRCREV = "c9297981b7c35ad9c2bf258e7c8d786a04d13378"

SRC_URI = "git://github.com/protocolbuffers/protobuf.git;branch=3.19.x;protocol=https \
           file://run-ptest \
           file://0001-protobuf-fix-configure-error.patch \
           file://0001-Makefile.am-include-descriptor.cc-when-building-libp.patch \
           file://0001-examples-Makefile-respect-CXX-LDFLAGS-variables-fix-.patch \
           file://0001-Fix-linking-error-with-ld-gold.patch \
           file://0001-Lower-init-prio-for-extension-attributes.patch \
           "
SRC_URI:append:mips:toolchain-clang = " file://0001-Fix-build-on-mips-clang.patch "
SRC_URI:append:mipsel:toolchain-clang = " file://0001-Fix-build-on-mips-clang.patch "

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig ptest

PACKAGECONFIG ??= ""
PACKAGECONFIG[python] = ",,"

EXTRA_OECONF += "--with-protoc=echo"

TEST_SRC_DIR = "examples"
LANG_SUPPORT = "cpp ${@bb.utils.contains('PACKAGECONFIG', 'python', 'python', '', d)}"

do_compile_ptest() {
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

PACKAGE_BEFORE_PN = "${PN}-compiler ${PN}-lite"

FILES:${PN}-compiler = "${bindir} ${libdir}/libprotoc${SOLIBS}"
FILES:${PN}-lite = "${libdir}/libprotobuf-lite${SOLIBS}"

RDEPENDS:${PN}-compiler = "${PN}"
RDEPENDS:${PN}-dev += "${PN}-compiler"
RDEPENDS:${PN}-ptest = "bash ${@bb.utils.contains('PACKAGECONFIG', 'python', 'python-protobuf', '', d)}"

MIPS_INSTRUCTION_SET = "mips"

BBCLASSEXTEND = "native nativesdk"

LDFLAGS:append:arm = " -latomic"
LDFLAGS:append:mips = " -latomic"
LDFLAGS:append:powerpc = " -latomic"
LDFLAGS:append:mipsel = " -latomic"
