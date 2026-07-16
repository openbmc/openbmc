SUMMARY = "A fast usermode x86 and x86-64 emulator for Arm64 Linux"
HOMEPAGE = "https://github.com/FEX-Emu/FEX"
LICENSE = "MIT & BSL-1.0 & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=70d20d502833c35d6d5a4f0ef5d9efcc"
LIC_FILES_CHKSUM += "file://External/range-v3/LICENSE.txt;md5=5dc23d5193abaedb6e42f05650004624"
LIC_FILES_CHKSUM += "file://External/xxhash/LICENSE;md5=13be6b481ff5616f77dda971191bb29b"

COMPATIBLE_HOST = "aarch64.*-linux"
COMPATIBLE_HOST:libc-musl = "null"

SRC_URI = " \
	git://github.com/FEX-Emu/FEX.git;name=fex;protocol=https;nobranch=1;tag=FEX-${PV} \
	git://github.com/Sonicadvance1/cpp-optparse.git;name=cpp-optparse;subdir=${S}/Source/Common/cpp-optparse;protocol=https;nobranch=1 \
	git://github.com/FEX-Emu/drm-headers.git;name=fex-drm;subdir=${S}/External/drm-headers;protocol=https;nobranch=1 \
	git://github.com/Cyan4973/xxHash.git;name=xxhash;subdir=${S}/External/xxhash;protocol=https;nobranch=1 \
	git://github.com/fmtlib/fmt.git;name=fmt;subdir=${S}/External/fmt;protocol=https;nobranch=1 \
	git://github.com/FEX-Emu/jemalloc.git;name=jemalloc-glibc;subdir=${S}/External/jemalloc_glibc;protocol=https;nobranch=1 \
	git://github.com/ericniebler/range-v3.git;name=range-v3;subdir=${S}/External/range-v3;protocol=https;nobranch=1 \
	git://github.com/FEX-Emu/rpmalloc.git;name=rpmalloc;subdir=${S}/External/rpmalloc;protocol=https;nobranch=1 \
	git://github.com/martinus/unordered_dense.git;name=unordered-dense;subdir=${S}/External/unordered_dense;protocol=https;nobranch=1 \
	file://0001-LinkerGC-Do-not-strip-binaries-at-link-time.patch \
"

SRCREV_FORMAT = "fex"
SRCREV_fex = "1cc4b93e7a71c883ec021b71359f136394dc1f3c"
SRCREV_cpp-optparse = "9f94388a339fcbb0bc95c17768eb786c85988f6e"
SRCREV_fex-drm = "3e49836995c1dcb3df709440ad2f270b569c6a5f"
SRCREV_xxhash = "e626a72bc2321cd320e953a0ccf1584cad60f363"
SRCREV_fmt = "407c905e45ad75fc29bf0f9bb7c5c2fd3475976f"
SRCREV_jemalloc-glibc = "8436195ad5e1bc347d9b39743af3d29abee59f06"
SRCREV_rpmalloc = "1d85c246cd827ead6865f4f880d4fef53f2b1864"
SRCREV_unordered-dense = "3234af2c03549bc85656bfd3a86993bf1cd8aef1"
SRCREV_range-v3 = "ca1388fb9da8e69314dda222dc7b139ca84e092f"

# fmt is built from the bundled External/fmt submodule rather than the system
# copy: FEX-2607 formats std::byte spans via fmt::join, which its pinned fmt
# 12.1.0 supports but oe-core's newer fmt (12.2.0) rejects. FEX only falls
# back to the bundled copy when find_package(fmt) fails, so
# CMAKE_DISABLE_FIND_PACKAGE_fmt is set below to stop it from picking up
# fmt-native (a transitive native dependency) from the native sysroot.
DEPENDS = " \
    catch2 \
    libdrm  \
    nasm-native \
    vulkan-headers \
"

PACKAGECONFIG = ""
PACKAGECONFIG[qt] = "-DBUILD_FEXCONFIG=ON,-DBUILD_FEXCONFIG=OFF,qtbase qttools-native qtquick3d"

EXTRA_OECMAKE += " \
	-DBUILD_TESTING=OFF \
	-DENABLE_CCACHE=OFF \
	-DENABLE_VIXL_DISASSEMBLER=OFF \
	-DENABLE_VIXL_SIMULATOR=OFF \
	-DCMAKE_DISABLE_FIND_PACKAGE_fmt=ON \
	-DDATA_DIRECTORY=${datadir} \
	-DQT_HOST_PATH:PATH=${RECIPE_SYSROOT_NATIVE}${prefix_native} \
"

inherit cmake ccache pkgconfig

LDFLAGS += "-fuse-ld=lld"

FILES:${PN} += "${datadir} ${libdir}/binfmt.d ${libdir}/libFEXCore.so"
FILES:${PN}-dev = "${includedir}"

# At the time of writing this, this recipe has no historical CVEs associated, and couldn't
# find a CPE to set. So ignore these unrelated vulnerabilities.
CVE_STATUS_GROUPS = "CVE_STATUS_WRONG_CPE"
CVE_STATUS_WRONG_CPE[status] = "cpe-incorrect: These vulnerabilities are for unrelated Fram's Fast File-EXchange application"
CVE_STATUS_WRONG_CPE = "CVE-2012-0869 CVE-2012-1293 CVE-2014-3875 CVE-2014-3876 CVE-2014-3877"
