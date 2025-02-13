SUMMARY = "Protocol Buffers - structured data serialisation mechanism"
DESCRIPTION = "Protocol Buffers are a way of encoding structured data in an \
efficient yet extensible format. Google uses Protocol Buffers for almost \
all of its internal RPC protocols and file formats."
HOMEPAGE = "https://github.com/google/protobuf"
SECTION = "console/tools"
LICENSE = "BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=37b5762e07f0af8c74ce80a8bda4266b \
    file://third_party/lunit/LICENSE;md5=99f08e72434dfa34fe0581d3dfb2d7f4 \
    file://third_party/utf8_range/LICENSE;md5=d4974d297231477b2ff507c35d61c13c \
"

DEPENDS = "zlib abseil-cpp jsoncpp"
DEPENDS:append:class-target = " protobuf-native"

SRCREV = "9d0ec0f92b5b5fdeeda11f9dcecc1872ff378014"

SRC_URI = "git://github.com/protocolbuffers/protobuf.git;branch=25.x;protocol=https \
           file://run-ptest \
           file://0001-examples-Makefile-respect-CXX-LDFLAGS-variables-fix-.patch \
           "
SRC_URI:append:mips:toolchain-clang = " file://0001-Fix-build-on-mips-clang.patch "
SRC_URI:append:mipsel:toolchain-clang = " file://0001-Fix-build-on-mips-clang.patch "

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.(25\.(\d+)))"

S = "${WORKDIR}/git"

CVE_PRODUCT = "google:protobuf protobuf:protobuf google-protobuf protobuf-cpp"

inherit cmake pkgconfig ptest

PACKAGECONFIG ??= ""
PACKAGECONFIG:class-native ?= "compiler"
PACKAGECONFIG:class-nativesdk ?= "compiler"
PACKAGECONFIG[python] = ",,"
PACKAGECONFIG[compiler] = "-Dprotobuf_BUILD_PROTOC_BINARIES=ON,-Dprotobuf_BUILD_PROTOC_BINARIES=OFF"

EXTRA_OECMAKE += "\
    -Dprotobuf_BUILD_SHARED_LIBS=ON \
    -Dprotobuf_BUILD_LIBPROTOC=ON \
    -Dprotobuf_BUILD_TESTS=OFF \
    -Dprotobuf_BUILD_EXAMPLES=OFF \
    -Dprotobuf_ABSL_PROVIDER="package" \
    -Dprotobuf_JSONCPP_PROVIDER="package" \
"

TEST_SRC_DIR = "examples"
LANG_SUPPORT = "cpp ${@bb.utils.contains('PACKAGECONFIG', 'python', 'python', '', d)}"

do_compile_ptest() {
	mkdir -p "${B}/${TEST_SRC_DIR}"

	# Add the location of the cross-compiled header and library files
	# which haven't been installed yet.
	cp "${B}/protobuf.pc" "${B}/${TEST_SRC_DIR}/protobuf.pc"
	cp ${S}/${TEST_SRC_DIR}/*.cc "${B}/${TEST_SRC_DIR}/"
	cp ${S}/${TEST_SRC_DIR}/*.proto "${B}/${TEST_SRC_DIR}/"
	cp ${S}/${TEST_SRC_DIR}/*.py "${B}/${TEST_SRC_DIR}/"
	cp ${S}/${TEST_SRC_DIR}/Makefile "${B}/${TEST_SRC_DIR}/"
	# Adapt protobuf.pc
	sed -e 's|libdir=|libdir=${PKG_CONFIG_SYSROOT_DIR}|' -i "${B}/${TEST_SRC_DIR}/protobuf.pc"
	sed -e 's|Cflags:|Cflags: -I${S}/src |' -i "${B}/${TEST_SRC_DIR}/protobuf.pc"
	sed -e 's|Cflags:|Cflags: -I${WORKDIR}/recipe-sysroot${includedir} |' -i "${B}/${TEST_SRC_DIR}/protobuf.pc"
	sed -e 's|Libs:|Libs: -L${B}|' -i "${B}/${TEST_SRC_DIR}/protobuf.pc"
	sed -e 's|Libs:|Libs: -L${WORKDIR}/recipe-sysroot/usr/lib |' -i "${B}/${TEST_SRC_DIR}/protobuf.pc"
	sed -e 's|Libs:|Libs: -labsl_log_internal_check_op |' -i "${B}/${TEST_SRC_DIR}/protobuf.pc"
	sed -e 's|Libs:|Libs: -labsl_log_internal_message |' -i "${B}/${TEST_SRC_DIR}/protobuf.pc"
	# Adapt uf8_range.pc
	cp "${B}/third_party/utf8_range/utf8_range.pc" "${B}/${TEST_SRC_DIR}/utf8_range.pc"
	sed -e 's|libdir=|libdir=${PKG_CONFIG_SYSROOT_DIR}|' -i "${B}/${TEST_SRC_DIR}/utf8_range.pc"
	sed -e 's|Libs:|Libs= -L${B}/third_party/utf8_range |' -i "${B}/${TEST_SRC_DIR}/utf8_range.pc"
	# Until out-of-tree build of examples is supported, we have to use this approach
	sed -e 's|../src/google/protobuf/.libs/timestamp.pb.o|${B}/CMakeFiles/libprotobuf.dir/src/google/protobuf/timestamp.pb.cc.o|' -i "${B}/${TEST_SRC_DIR}/Makefile"
	export PKG_CONFIG_PATH="${B}/${TEST_SRC_DIR}"

	# Save the pkgcfg sysroot variable, and update it to nothing so
	# that it doesn't append the sysroot to the beginning of paths.
	# The header and library files aren't installed to the target
	# system yet.  So the absolute paths were specified above.
	save_pkg_config_sysroot_dir=$PKG_CONFIG_SYSROOT_DIR
	export PKG_CONFIG_SYSROOT_DIR=

	# Compile the tests
	for lang in ${LANG_SUPPORT}; do
		oe_runmake -C "${B}/${TEST_SRC_DIR}" ${lang}
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
	cp "${B}/${TEST_SRC_DIR}/addressbook_pb2.py" "${D}/${PTEST_PATH}"
	cd "$olddir"
}

PACKAGE_BEFORE_PN = "${PN}-compiler ${PN}-lite"

FILES:${PN}-compiler = "${bindir} ${libdir}/libprotoc${SOLIBS}"
FILES:${PN}-lite = "${libdir}/libprotobuf-lite${SOLIBS}"

# CMake requires protoc binary to exist in sysroot, even if it has wrong architecture.
SYSROOT_DIRS += "${bindir}"

RDEPENDS:${PN}-compiler = "${PN}"
RDEPENDS:${PN}-dev += "${@bb.utils.contains('PACKAGECONFIG', 'compiler', '${PN}-compiler', '', d)}"
RDEPENDS:${PN}-ptest = "bash ${@bb.utils.contains('PACKAGECONFIG', 'python', 'python3-protobuf', '', d)}"

MIPS_INSTRUCTION_SET = "mips"

BBCLASSEXTEND = "native nativesdk"
